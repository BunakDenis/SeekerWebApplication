package com.example.telegram.constanst;

import com.example.data.models.enums.UserRoles;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

public class TelegramBotConstants {

    public static final User TELEGRAM_API_USER_FOR_TESTS = new User(12345L, "Elon", false, "Mask",
            "max", "ru",
            null, null, null, null, null);

    public static final com.example.telegram.bot.entity.User USER_FOR_TESTS = new com.example.telegram.bot.entity.User(
            1L, "testUser", "test@exemple.com", UserRoles.ADMIN.getRole(), true, null, null
    );

    public static final Chat CHAT_FOR_TESTS = new Chat(123456789L, "private");

}
