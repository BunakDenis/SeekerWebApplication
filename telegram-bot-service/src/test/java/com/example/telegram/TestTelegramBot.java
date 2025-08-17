package com.example.telegram;


import com.example.utils.file.loader.EnvLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.example.telegram.constanst.TelegramBotConstants.*;
import static com.example.telegram.bot.message.MessageProvider.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class TestTelegramBot {

    private static Dotenv dotenv = EnvLoader.DOTENV;
    private static final Logger logger = LoggerFactory.getLogger(TestTelegramBot.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

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

        logger.info("Загрузка образа {}", TELEGRAM_BOT_SERVICE_IMAGE);

        if (System.getenv("GITHUB_ACTIONS") != null) {
            DockerImageName ghcrImage = DockerImageName.parse(TELEGRAM_BOT_SERVICE_IMAGE);

            logger.info(ghcrImage.toString());

            botContainer = new GenericContainer<>(ghcrImage);
        } else {
            botContainer = new GenericContainer<>(TELEGRAM_BOT_SERVICE_IMAGE);
        }

        botContainer
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

    private String getWebhookUrl() {
        return "http://" + botContainer.getHost() + ":" +
                botContainer.getMappedPort(Integer.parseInt(TELEGRAM_BOT_PORT)) +
                TELEGRAM_BOT_WEB_HOOK_PATH;
    }

    private HttpResponse<String> sendUpdate(Update update) throws Exception {

        logger.info("Web hook URL {}", getWebhookUrl());

        String jsonUpdate = objectMapper.writeValueAsString(update);

        logger.info("Отправка update телеграм боту {}", jsonUpdate);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getWebhookUrl()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonUpdate))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void testContainerIsRunning() {
        assertTrue(botContainer.isRunning());
    }

    @Test
    void testStartCommand() throws Exception {
        Update update = new Update();
        Message message = new Message();
        message.setText("/start");
        message.setChat(new Chat(123456789L, "private"));
        message.setFrom(USER_FOR_TESTS);
        update.setMessage(message);

        HttpResponse<String> response = sendUpdate(update);

        logger.info("Код ответа от сервера телеграм бота {}", response.statusCode());

        logger.info(response.body());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(START_MSG));
    }

}
