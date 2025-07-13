package com.example.utils.file.changer;

import lombok.Data;
import lombok.extern.log4j.Log4j;

@Data
@Log4j
public class FileExtensionChanger {

    public static String change(String fileName, String newExtension) {

        if (fileName == null || fileName.isEmpty()) {
            log.debug("filename is null or empty");
            return fileName;
        }

        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1) {
            // Нет расширения, просто добавляем новое
            return fileName + "." + newExtension;
        } else {
            // Заменяем расширение
            return fileName.substring(0, dotIndex) + "." + newExtension;
        }
    }
}
