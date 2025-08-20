package com.example.telegram.bot.utils.update;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
@Data
public class UpdateUtilsService {

    public static User getTelegramUser(Update update) {
        return update.getMessage().getFrom();
    }
    public static long getChatId(Update update) {
        return update.getMessage().getChatId();
    }

    public static Message getMessage(Update update) {return update.getMessage();}

    public static String getMessageText(Update update) {
        return update.getMessage().getText();
    }

}
