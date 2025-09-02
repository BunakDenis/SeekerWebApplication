package com.example.telegram.bot.utils.update;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

@Service
@Data
public class UpdateUtilsService {


    public static User getTelegramUser(Update update) {
        return update.getMessage().getFrom();
    }
    public static Long getTelegramUserId(Update update) {
        return getTelegramUser(update).getId();
    }
    public static String getTelegramUserFullName(Update update) {
        StringBuilder result = new StringBuilder();
        User telegramUser = getTelegramUser(update);
        String userFirstName = telegramUser.getFirstName();
        String userLastName = telegramUser.getLastName();

        if (Objects.nonNull(userFirstName) || !userFirstName.isEmpty()) result.append(userFirstName);

        if (Objects.nonNull(userLastName) || !userLastName.isEmpty()) {
            result.append(" ");
            result.append(userLastName);
        }

        return result.toString();
    }
    public static Long getChatId(Update update) {
        return update.getMessage().getChatId();
    }
    public static String getStringChatId(Update update) {
        return Long.toString(update.getMessage().getChatId());
    }
    public static Message getMessage(Update update) {return update.getMessage();}
    public static String getMessageText(Update update) {
        return update.getMessage().getText();
    }
    public static String getCommandValue(Update update) {
        return update.getMessage().getText().split(" ")[0];
    }

}
