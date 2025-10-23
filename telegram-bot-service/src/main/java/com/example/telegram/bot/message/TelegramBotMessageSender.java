package com.example.telegram.bot.message;

import com.example.telegram.bot.TelegramBot;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class TelegramBotMessageSender {

    @Value("${telegram.bot.max.length}")
    private int maxMessageLength;

    // Регистронезависимый поиск тега <a ...>...</a>
    private static final Pattern ANCHOR_TAG_PATTERN =
            Pattern.compile("(?i)<\\s*a\\b[^>]*>.*?</\\s*a\\s*>", Pattern.DOTALL);

    private final ApplicationContext applicationContext;

    /**
     * Отправляет текст в чат, разбивая его на несколько сообщений, если он превышает максимальную длину.
     *
     * @param chatId ID чата
     * @param text   Текст для отправки
     */
    public void sendMessage(Long chatId, String text) {
        log.debug("Отправляю сообщение в чат с id={}, сообщение = {}", chatId, text);
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
        log.debug("Отправляю сообщение в чат с id={}, сообщение = {}", msg.getChatId(), msg.getText());
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

        if (containsAnchorTag(text)) {
            // Для совместимости используем строковый вариант. В новых версиях можно использовать ParseMode.HTML
            message.setParseMode("html");
            message.setDisableWebPagePreview(true);
        } else {
            // Очищаем parseMode — оставляем как по умолчанию
            message.setParseMode(null);
        }

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


    /**
     * Простая проверка — содержит ли переданная строка HTML-тег <a ...>...</a>.
     *
     * @param text входной текст (может быть null)
     * @return true если найден <a>...</a>, иначе false
     */
    public static boolean containsAnchorTag(String text) {
        if (text == null) return false;
        return ANCHOR_TAG_PATTERN.matcher(text).find();
    }

}
