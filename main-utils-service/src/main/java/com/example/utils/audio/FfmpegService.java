package com.example.utils.audio;


import com.example.utils.file.changer.FileExtensionChanger;
import lombok.extern.log4j.Log4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j
public class FfmpegService {

    private static String beguilingDrafterModelPath = "../models/ffmpeg/beguiling-drafter/bd.rnnn";
    private static String conjoinedBurgersModelPath = "../models/ffmpeg/conjoined-burgers/cb.rnnn";
    private static String leavenedQuislingModelPath = "../models/ffmpeg/leavened-quisling/lq.rnnn";
    private static String marathonPrescriptionModelPath = "../models/ffmpeg/marathon-prescription/mp.rnnn";
    private static String somnolentHogwashModelPath = "../models/ffmpeg/beguiling-drafter/bd.rnnn";

    public static File convertAudio(String inputFilePath) throws IOException, InterruptedException {

        String outputFilePath = FileExtensionChanger.change(inputFilePath, "wav");

        // 1. Создание списка аргументов для команды FFmpeg
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(inputFilePath);
        command.add("-acodec");
        command.add("pcm_s16le");
        command.add("-ac");
        command.add("1");
        command.add("-ar");
        command.add("16000");
        command.add(outputFilePath);

        log.debug("FFmpeg команда: " + String.join(" ", command));

        // 2. Создание ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // 3. Установка рабочей директории (папки, где находятся файлы)
        File workingDirectory = new File(new File(inputFilePath).getParent()); // Получаем папку из пути входного файла
        processBuilder.directory(workingDirectory);

        // 4. Перенаправление ошибок в стандартный вывод (для отладки)
        processBuilder.redirectErrorStream(true);

        // 5. Запуск процесса
        Process process = processBuilder.start();

        log.debug("Начало конвертации аудио файла " + inputFilePath);

        // Ждём максимум 5 минут (можно изменить)
        boolean finished = process.waitFor(5, TimeUnit.MINUTES);
        if (!finished) {
            process.destroyForcibly();
            throw new IOException(
                    "FFmpeg процесс конвертации файла " +
                            inputFilePath +
                            "не завершился в течение 5 минут и был принудительно остановлен."
            );
        }

        // 6. Ожидание завершения процесса
        int exitCode = process.exitValue();

        // 7. Проверка кода завершения
        if (exitCode != 0) {
            log.error("FFmpeg process exited with error code: " + exitCode);

            // Чтение вывода FFmpeg (для отладки)
            java.io.InputStream inputStream = process.getInputStream();
            java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";

            log.error("FFmpeg output:\n" + result);

            throw new IOException("FFmpeg conversion failed with exit code: " + exitCode);
        }

        log.debug("Конвертация файла " + inputFilePath + " > " + outputFilePath + " завершена успешно!");

        return new File(outputFilePath);
    }

    public static void denoiseAudio(File inputFile, File outputFile) throws IOException, InterruptedException {
        // Удаление выходного файла, если он уже существует
        if (outputFile.exists()) {
            if (!outputFile.delete()) {
                log.error("Не удалось удалить существующий файл: " + outputFile.getAbsolutePath());
                throw new IOException("Не удалось удалить существующий файл: " + outputFile.getAbsolutePath());
            }
        }

        // Построение команды FFmpeg
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-y");
        command.add("-i");
        command.add(inputFile.getName());
        command.add("-af");
        String filter = String.format("arnndn=m='%s'", beguilingDrafterModelPath);
        command.add(filter);
        command.add(outputFile.getName());

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(inputFile.getParentFile());
        processBuilder.redirectErrorStream(true);

        log.debug("FFmpeg команда: " + String.join(" ", command));
        log.debug("Рабочая директория: " + processBuilder.directory());

        Process process = processBuilder.start();

        // Чтение вывода в отдельном потоке, чтобы избежать блокировки
        Thread outputReader = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("[FFmpeg] " + line);
                }
            } catch (IOException e) {
                log.error("Ошибка при чтении вывода FFmpeg: ", e);
            }
        });

        outputReader.start();

        // Ждём максимум 5 минут (можно изменить)
        boolean finished = process.waitFor(5, TimeUnit.MINUTES);
        if (!finished) {
            process.destroyForcibly();
            throw new IOException(
                    "FFmpeg процесс \"чистки\" файла " +
                    inputFile +
                    "не завершился в течение 5 минут и был принудительно остановлен."
            );
        }

        int exitCode = process.exitValue();
        outputReader.join(); // Дождаться завершения потока вывода

        if (exitCode != 0) {
            throw new IOException("FFmpeg завершился с ошибкой (код " + exitCode + ")");
        }

        log.debug("Успешная обработка файла: " + inputFile.getAbsolutePath() +
                " → " + outputFile.getAbsolutePath());
    }

}
