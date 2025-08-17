package com.example.utils.file.loader;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class EnvLoader {

    public static final Dotenv DOTENV = createDotenv();

    private static Dotenv createDotenv() {
        // Проверяем, запущено ли в GitHub Actions
        if (System.getenv("GITHUB_ACTIONS") != null) {

            log.info("GITHUB_ACTIONS = " + System.getenv("GITHUB_ACTIONS"));
            log.info("Запущено в GitHub Actions, загрузка .env пропущена");

            return null; // Не загружаем .env в GitHub Actions
        }

        // Иначе загружаем из .env
        log.info("Загрузка .env файла");

        return Dotenv.configure()
                .directory("../.env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }

    public static String get(String key) {

        String value = System.getenv(key); // Сначала проверяем системные переменные

        if (value == null && DOTENV != null) { // Если не найдено в системе и DOTENV загружен, используем .env
            value = DOTENV.get(key);
        }

        if (value == null) {
            log.warn("Переменная окружения {} не найдена ни в системных переменных, ни в .env файле", key);
        }

        return value;
    }
}
