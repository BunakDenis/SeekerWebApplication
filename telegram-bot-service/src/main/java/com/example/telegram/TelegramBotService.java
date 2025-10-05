package com.example.telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.telegram", "com.example.utils", "com.example.data.models"})
public class TelegramBotService {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotService.class, args);
    }
}
