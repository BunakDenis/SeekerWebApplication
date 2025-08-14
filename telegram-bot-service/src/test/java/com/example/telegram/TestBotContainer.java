package com.example.telegram;


import com.example.utils.file.loader.EnvLoader;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class TestBotContainer {

    private static Dotenv dotenv = EnvLoader.DOTENV;
    private static final Logger logger = LoggerFactory.getLogger(TestBotContainer.class);

    @Container
    private static GenericContainer<?> botContainer = new GenericContainer<>(
            dotenv.get("TELEGRAM_BOT_SERVICE_IMAGE")
    )
            .withExposedPorts(Integer.valueOf(dotenv.get("TELEGRAM_BOT_PORT")))
            .withEnv("SPRING_PROFILES_ACTIVE", dotenv.get("SPRING_PROFILES_ACTIVE"))
            .withEnv("TRUTH_SEEKER_OFFICE_IP_ADDRESS", dotenv.get("TRUTH_SEEKER_OFFICE_IP_ADDRESS"))
            .withEnv("TELEGRAM_BOT_NAME", dotenv.get("TELEGRAM_BOT_NAME"))
            .withEnv("TELEGRAM_BOT_TOKEN", dotenv.get("TELEGRAM_BOT_TOKEN"))
            .withEnv("TELEGRAM_BOT_WEB_HOOK_URL", dotenv.get("TELEGRAM_BOT_WEB_HOOK_URL"))
            .withEnv("TELEGRAM_BOT_WEB_HOOK_PATH", dotenv.get("TELEGRAM_BOT_WEB_HOOK_PATH"))
            .withEnv("DATA_PROVIDE_SERVICE_PORT", dotenv.get("DATA_PROVIDE_SERVICE_PORT"))
            .withEnv("DB_API_VERSION", dotenv.get("DB_API_VERSION"))
            .withEnv("USEFUL_TOOLS_API_PORT", dotenv.get("USEFUL_TOOLS_API_PORT"))
            .withEnv("USEFUL_TOOLS_API_URL", dotenv.get("USEFUL_TOOLS_API_URL"))
            .withEnv("USEFUL_TOOLS_API_FILE_SERVICE_ENDPOINT", dotenv.get("USEFUL_TOOLS_API_FILE_SERVICE_ENDPOINT"))
            .withEnv("TELEGRAM_BOT_PORT", dotenv.get("TELEGRAM_BOT_PORT"))
            .withLogConsumer(new Slf4jLogConsumer(logger));

    @Test
    void testContainerIsRunning() {
        assertTrue(botContainer.isRunning());
    }
}
