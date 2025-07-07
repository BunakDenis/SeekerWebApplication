package com.example.telegram.bot.message;

import com.example.telegram.bot.TelegramBot;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
@RequiredArgsConstructor
@Log4j
public class TelegramBotMessageSender {

    @Value("${TELEGRAM_BOT_MAX_MESSAGE_LENGTH}")
    private int maxMessageLength;

    private final ApplicationContext applicationContext;

    /**
     * Отправляет текст в чат, разбивая его на несколько сообщений, если он превышает максимальную длину.
     *
     * @param chatId ID чата
     * @param text   Текст для отправки
     */
    public void sendMessage(Long chatId, String text) {
        List<String> messageParts = splitMessage(text);
        for (String part : messageParts) {
            sendSingleMessage(chatId, part);
        }
    }

    /**
     * Отправляет текст в чат, разбивая его на несколько сообщений, если он превышает максимальную длину.
     *
     * @param msg Сообщение для отправки
     */
    public void sendMessage(SendMessage msg) {
        if (isTextValidForSending(msg.getText())) {
            executeSafely(msg);
        } else {
            sendMessage(Long.valueOf(msg.getChatId()), msg.getText());
        }
    }

    /**
     * Отправляет текст в чат, разбивая его на несколько сообщений, если он превышает максимальную длину.
     *
     * @param audio Сообщение для отправки
     */
    public void sendAudio(SendAudio audio) {
        executeSafely(audio);
    }

    /**
     * Разбивает текст на части заданной максимальной длины.
     *
     * @param text Текст для разбиения
     *
     * @return Список частей текста
     */
    private List<String> splitMessage(String text) {
        List<String> parts = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return parts; // Возвращаем пустой список для пустого текста
        }

        int length = text.length();
        for (int i = 0; i < length; i += maxMessageLength) {
            int end = Math.min(length, i + maxMessageLength);
            parts.add(text.substring(i, end));
        }
        return parts;
    }

    /**
     * Отправляет одно сообщение в чат.
     *
     * @param chatId ID чата
     * @param text   Текст сообщения
     */
    private void sendSingleMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        executeSafely(message);
    }

    /**
     * Безопасно выполняет отправку сообщения.
     *
     * @param message Сообщение для отправки
     */
    private void executeSafely(SendMessage message) {
        try {
            TelegramBot bot = applicationContext.getBean(TelegramBot.class);
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}" + e.getMessage());
        }
    }

    /**
     * Безопасно выполняет отправку сообщения.
     *
     * @param message Аудио сообщение для отправки
     */
    private void executeSafely(SendAudio message) {
        try {
            TelegramBot bot = applicationContext.getBean(TelegramBot.class);
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}" + e.getMessage());
        }
    }

    /**
     * Проверка текста соответствия максимальной длине сообщения
     *
     * @param text Текст для проверки
     *
     */
    private boolean isTextValidForSending(String text) {
        return text.length() < maxMessageLength;
    }

}
