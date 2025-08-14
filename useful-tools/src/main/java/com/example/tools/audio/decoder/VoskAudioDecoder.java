package com.example.tools.audio.decoder;


import com.example.utils.datetime.DateTimeService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.StringJoiner;


@Data
@Log4j2
public class VoskAudioDecoder implements AudioDecoder {
    private Model model;
    private Recognizer recognizer;
    @Autowired
    private VoskResult decodeResult;
    @Value("${spring.profiles.active}")
    private String springActiveProfile;
    private static final String MODEL_PATH_IN_CONTAINER = "/app/models/vosk-model-ru";
    private static final String MODEL_PATH_IN_IDE = "models/vosk-model-ru";

    @PostConstruct
    public void init() {
        try {
            String modelPath = null;

            // 1. Проверяем, запущено ли приложение в Docker-контейнере
            boolean isRunningInDocker = isRunningInDocker();

            if (isRunningInDocker) {
                // 2. Если запущено в Docker, используем путь внутри контейнера
                modelPath = MODEL_PATH_IN_CONTAINER;
                log.debug("Приложение запущено в Docker. Используем путь к модели: " + modelPath);

            } else {
                // 3. Если запущено в IDE, используем относительный путь
                modelPath = MODEL_PATH_IN_IDE;
                log.debug("Приложение запущено в IDE. Используем путь к модели: " + modelPath);
            }

            // 4. Загружаем модель
            this.model = new Model(modelPath);
            log.debug("Загрузка декодера Vosk прошла успешно!");

        } catch (IOException e) {
            log.error("Ошибка загрузки модели Vosk: " + e.getMessage(), e);
            throw new RuntimeException("Не удалось загрузить модель Vosk", e);
        }
    }

    // Метод для определения, запущено ли приложение в Docker-контейнере
    private boolean isRunningInDocker() {
        /*
        log.debug("Переменная springActiveProfile = " + springActiveProfile);

        boolean result = !springActiveProfile.equalsIgnoreCase("dev");

         */

        return true;
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

    // Закрытие модели при уничтожении бина
    @PreDestroy
    public void destroy() {
        if (model != null) {
            model.close();
            log.debug("Vosk model closed");
        }
    }
}
