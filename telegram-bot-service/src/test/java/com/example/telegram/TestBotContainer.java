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

    private static final String TELEGRAM_BOT_SERVICE_IMAGE = EnvLoader.get("TELEGRAM_BOT_SERVICE_IMAGE");
    private static final String TELEGRAM_BOT_PORT = EnvLoader.get("TELEGRAM_BOT_PORT");
    private static final String SPRING_PROFILES_ACTIVE = EnvLoader.get("SPRING_PROFILES_ACTIVE");
    private static final String TRUTH_SEEKER_OFFICE_IP_ADDRESS = EnvLoader.get("TRUTH_SEEKER_OFFICE_IP_ADDRESS");
    private static final String TELEGRAM_BOT_NAME = EnvLoader.get("TELEGRAM_BOT_NAME");
    private static final String TELEGRAM_BOT_TOKEN = EnvLoader.get("TELEGRAM_BOT_TOKEN");
    private static final String TELEGRAM_BOT_WEB_HOOK_URL = EnvLoader.get("TELEGRAM_BOT_WEB_HOOK_URL");
    private static final String TELEGRAM_BOT_WEB_HOOK_PATH = EnvLoader.get("TELEGRAM_BOT_WEB_HOOK_PATH");
    private static final String DATA_PROVIDE_SERVICE_PORT = EnvLoader.get("DATA_PROVIDE_SERVICE_PORT");
    private static final String DB_API_VERSION = EnvLoader.get("DB_API_VERSION");
    private static final String USEFUL_TOOLS_API_PORT = EnvLoader.get("USEFUL_TOOLS_API_PORT");
    private static final String USEFUL_TOOLS_API_URL = EnvLoader.get("USEFUL_TOOLS_API_URL");
    private static final String USEFUL_TOOLS_API_FILE_SERVICE_ENDPOINT = EnvLoader.get("USEFUL_TOOLS_API_FILE_SERVICE_ENDPOINT");
    @Container
    private static GenericContainer<?> botContainer;

    static {
        if (TELEGRAM_BOT_SERVICE_IMAGE == null || TELEGRAM_BOT_SERVICE_IMAGE.isEmpty()) {
            logger.error("Переменная окружения TELEGRAM_BOT_SERVICE_IMAGE не определена!");
            throw new IllegalStateException("TELEGRAM_BOT_SERVICE_IMAGE не определена");
        }

        botContainer = new GenericContainer<>(TELEGRAM_BOT_SERVICE_IMAGE)
                .withExposedPorts(Integer.parseInt(TELEGRAM_BOT_PORT))
                .withEnv("SPRING_PROFILES_ACTIVE", SPRING_PROFILES_ACTIVE)
                .withEnv("TRUTH_SEEKER_OFFICE_IP_ADDRESS", TRUTH_SEEKER_OFFICE_IP_ADDRESS)
                .withEnv("TELEGRAM_BOT_NAME", TELEGRAM_BOT_NAME)
                .withEnv("TELEGRAM_BOT_TOKEN", TELEGRAM_BOT_TOKEN)
                .withEnv("TELEGRAM_BOT_WEB_HOOK_URL", TELEGRAM_BOT_WEB_HOOK_URL)
                .withEnv("TELEGRAM_BOT_WEB_HOOK_PATH", TELEGRAM_BOT_WEB_HOOK_PATH)
                .withEnv("DATA_PROVIDE_SERVICE_PORT", DATA_PROVIDE_SERVICE_PORT)
                .withEnv("DB_API_VERSION", DB_API_VERSION)
                .withEnv("USEFUL_TOOLS_API_PORT", USEFUL_TOOLS_API_PORT)
                .withEnv("USEFUL_TOOLS_API_URL", USEFUL_TOOLS_API_URL)
                .withEnv("USEFUL_TOOLS_API_FILE_SERVICE_ENDPOINT", USEFUL_TOOLS_API_FILE_SERVICE_ENDPOINT)
                .withEnv("TELEGRAM_BOT_PORT", TELEGRAM_BOT_PORT)
                .withLogConsumer(new Slf4jLogConsumer(logger));
    }

    @Test
    void testContainerIsRunning() {
        assertTrue(botContainer.isRunning());
    }
}
