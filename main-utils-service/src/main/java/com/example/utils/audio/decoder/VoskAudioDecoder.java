package com.example.utils.audio.decoder;

import com.example.utils.datetime.DateTimeService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Map;
import java.util.StringJoiner;

@Component
@Data
@Log4j
public class VoskAudioDecoder implements AudioDecoder {

    private Model model;
    private Recognizer recognizer;

    @Autowired
    private VoskResult decodeResult;

    @PostConstruct
    public void init() {
        try {
            Path modelPath = extractDirectoryFromResources("models/vosk-model-ru");

            this.model = new Model(modelPath.toString());

            log.debug("Загрузка декодера Vosk прошла успешно!");
        } catch (IOException e) {
            log.error("Ошибка загрузки модели Vosk: " + e.getMessage(), e);
            throw new RuntimeException("Не удалось загрузить модель Vosk", e);
        }
    }

    @Override
    public String decode(String audioPath) {
        StringJoiner result = new StringJoiner("\n");
        File sourceFile = new File(audioPath);

        log.debug("Vosk audio decoder start decoding file - " + audioPath +
                "\n at " + DateTimeService.getNowDateTime());

        try (AudioInputStream ais = AudioSystem.getAudioInputStream(sourceFile)) {
            // Convert to PCM 16kHz mono 16-bit
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    16000,
                    16,
                    1,
                    2,
                    16000,
                    false);

            // Проверяем формат аудио перед конвертацией
            AudioFormat sourceFormat = ais.getFormat();
            log.debug("Source audio format: " + sourceFormat);
            log.debug("Target audio format: " + targetFormat);

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, targetFormat);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(targetFormat);
            line.start();

            // 2. Распознавание речи
            int bytesRead;
            byte[] buffer = new byte[4096];

            try (AudioInputStream pcmStream = AudioSystem.getAudioInputStream(targetFormat, ais)) {

                // Создаем recognizer для каждого файла
                recognizer = new Recognizer(model, targetFormat.getSampleRate());

                while ((bytesRead = pcmStream.read(buffer)) != -1) {
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        decodeResult.setText(recognizer.getResult());
                        String serializeText = decodeResult.getSerializeText();
                        result.add(serializeText);
                    }
                }

                decodeResult.setText(recognizer.getFinalResult());
                String serializeFinalText = decodeResult.getSerializeText();
                result.add(serializeFinalText);

                log.debug("Vosk audio decoded file - " + audioPath +
                        "\n at " + DateTimeService.getNowDateTime());

                log.debug("Декодированный текст: \n" + result);

                // 3. Закрытие ресурсов
                line.drain();
                line.close();
            } finally {
                // Закрываем recognizer после использования
                if (recognizer != null) {
                    recognizer.close();
                }
            }
        } catch (Exception e) {
            log.error("Vosk decoding error - " + e.getMessage(), e);
        }
        return result.toString();
    }

    /**
     * Извлекает содержимое каталога из classpath в temp-директорию.
     */
    private Path extractDirectoryFromResources(String resourceDirPath) throws IOException {
        URL resourceUrl = getClass().getClassLoader().getResource(resourceDirPath);

        if (resourceUrl == null) {
            throw new FileNotFoundException("Resource not found: " + resourceDirPath);
        }

        if ("file".equals(resourceUrl.getProtocol())) {
            try {
                return Paths.get(resourceUrl.toURI());
            } catch (URISyntaxException e) {
                throw new IOException("Invalid URI for resource: " + resourceUrl, e);
            }
        }

        if ("jar".equals(resourceUrl.getProtocol()) || resourceUrl.toString().startsWith("jar:")) {
            Path tempDir = Files.createTempDirectory("vosk_model_");
            tempDir.toFile().deleteOnExit();

            try (FileSystem fs = FileSystems.newFileSystem(resourceUrl.toURI(), Map.of())) {
                Path jarDir = fs.getPath("/" + resourceDirPath);
                Files.walk(jarDir).forEach(sourcePath -> {
                    try {
                        Path destPath = tempDir.resolve(jarDir.relativize(sourcePath).toString());
                        if (Files.isDirectory(sourcePath)) {
                            Files.createDirectories(destPath);
                        } else {
                            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            } catch (Exception e) {
                throw new IOException("Failed to extract Vosk model from JAR", e);
            }

            return tempDir;
        }

        throw new IOException("Unsupported resource protocol: " + resourceUrl.getProtocol());
    }

    // Закрытие модели при уничтожении бина
    @PreDestroy
    public void destroy() {
        if (model != null) {
            model.close();
            log.debug("Vosk model closed");
        }
    }
}
