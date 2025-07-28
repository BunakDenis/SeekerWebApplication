package com.example.telegram;

import com.example.telegram.config.EnvLoader;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class TestBotContainer {

    private static Dotenv dotenv = EnvLoader.DOTENV;
    private static final Logger logger = LoggerFactory.getLogger(TestBotContainer.class);

    @Container
    private static GenericContainer<?> botContainer = new GenericContainer<>(
            "bunakdenis/telegram-bot-service:latest"
    )
            .withExposedPorts(8080)
            .withEnv("SPRING_PROFILES_ACTIVE", dotenv.get("SPRING_PROFILES_ACTIVE"))
            .withEnv("TELEGRAM_BOT_NAME", dotenv.get("DEV_TELEGRAM_BOT_NAME"))
            .withEnv("TELEGRAM_BOT_TOKEN", dotenv.get("DEV_TELEGRAM_BOT_TOKEN"))
            .withEnv("API_USEFUL_TOOLS_URL", dotenv.get("DEV_API_USEFUL_TOOLS_URL"))
            .withEnv("API_USEFUL_TOOLS_FILE_SERVICE_ENDPOINT", dotenv.get("DEV_API_USEFUL_TOOLS_FILE_SERVICE_ENDPOINT"))
            .withEnv("PORT", dotenv.get("PORT"))
            .withLogConsumer(new Slf4jLogConsumer(logger));

    @Test
    void testContainerIsRunning() {
        assertTrue(botContainer.isRunning());
    }
}
