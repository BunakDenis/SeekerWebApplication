package com.example.telegram;


import com.example.telegram.bot.TelegramBot;
import com.example.telegram.config.EnvLoader;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
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
            .withExposedPorts(8080)
            .withEnv("SPRING_PROFILES_ACTIVE", dotenv.get("SPRING_PROFILES_ACTIVE"))
            .withEnv("TELEGRAM_BOT_NAME", dotenv.get("DEV_TELEGRAM_BOT_NAME"))
            .withEnv("TELEGRAM_BOT_TOKEN", dotenv.get("DEV_TELEGRAM_BOT_TOKEN"))
            .withEnv("API_USEFUL_TOOLS_URL", dotenv.get("DEV_API_USEFUL_TOOLS_URL"))
            .withEnv("API_USEFUL_TOOLS_FILE_SERVICE_ENDPOINT", dotenv.get("DEV_API_USEFUL_TOOLS_FILE_SERVICE_ENDPOINT"))
            .withEnv("PORT", dotenv.get("PORT"))
            .withLogConsumer(new Slf4jLogConsumer(logger));

    @SpyBean
    private TelegramBot bot;

    @Test
    void testContainerIsRunning() {
        assertTrue(botContainer.isRunning());
    }

/*
    @Test
    void testStartCommand() throws TelegramApiException {
        // Arrange: создаём поддельный апдейт с командой /start
        Update update = new Update();

        Message message = new Message();
        message.setText("/start");
        Chat chat = new Chat();
        chat.setId(123456789L);
        message.setChat(chat);
        update.setMessage(message);

        // Act
        bot.onUpdateReceived(update);

        // Assert: проверяем, что execute был вызван с нужным сообщением
        SendMessage expected = new SendMessage("123456789", "Привет! Я бот.");
        Mockito.verify(bot, Mockito.times(1)).execute(Mockito.refEq(expected));
    }
*/
}
