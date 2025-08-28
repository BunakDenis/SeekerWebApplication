package com.example.telegram.bot.queries.impl;

import com.example.telegram.bot.queries.QueryHandler;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DecodeAudioQueryHandlerImpl implements QueryHandler {

    @Override
    public SendMessage apply(Update update) {
        String chatId = UpdateUtilsService.getStringChatId(update);

        return new SendMessage(chatId, "Загрузите аудио для декодирования.");
    }
}
