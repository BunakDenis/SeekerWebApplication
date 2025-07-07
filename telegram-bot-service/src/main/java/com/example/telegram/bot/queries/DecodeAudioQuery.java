package com.example.telegram.bot.queries;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DecodeAudioQuery implements Query {

    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();

        return new SendMessage(String.valueOf(chatId), "Загрузите аудио для декодирования.");
    }
}
