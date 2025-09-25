package com.example.telegram;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.example.telegram", "com.example.utils", "com.example.data.models"})
public class TestTelegramBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(TestTelegramBotApplication.class, args);
	}
}
