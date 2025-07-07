package com.example.utils;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UtilsApplication {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .directory(".") // Ищем .env в текущей директории
                .ignoreIfMissing() // Игнорировать, если файл не найден (опционально, но полезно)
                .load();

        SpringApplication.run(UtilsApplication.class, args);

    }
}
