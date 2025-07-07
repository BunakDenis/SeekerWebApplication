package com.example.utils.file.changer;

import lombok.extern.log4j.Log4j;

@Log4j
public class FileNameChanger {

    public static String addSuffix(String filename, String suffix) {
        if (filename == null || filename.isEmpty()) {
            log.error("Filename is null or empty");
            return null;
        }

        int dotIndex = filename.lastIndexOf('.');

        if (dotIndex == -1) {
            // Нет расширения, просто добавляем суффикс
            return filename + suffix;
        } else {
            // Добавляем суффикс перед расширением
            return filename.substring(0, dotIndex) + suffix + filename.substring(dotIndex);
        }
    }

}
