package com.example.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.example.telegram", "com.example.utils", "com.example.data.models"})
public class TestTelegramBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(TestTelegramBotApplication.class, args);
	}

}
