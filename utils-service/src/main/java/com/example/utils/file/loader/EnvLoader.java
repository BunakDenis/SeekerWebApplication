package com.example.utils.file.loader;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class EnvLoader {

    public static final Dotenv DOTENV = createDotenv();

    private static Dotenv createDotenv() {
        // Сначала пробуем загрузить переменные из окружения системы
        if (System.getenv("TELEGRAM_BOT_SERVICE_IMAGE") != null) {
            log.debug("Using system environment variables, .env loading skipped");
            return null; // Не загружаем .env, если переменные есть в системе
        }

        // Если переменные в системе не найдены, загружаем из .env
        log.debug("Loading .env file");
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
