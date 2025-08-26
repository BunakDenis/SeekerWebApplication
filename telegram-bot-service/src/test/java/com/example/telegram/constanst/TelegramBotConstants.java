package com.example.telegram.constanst;

import com.example.data.models.enums.UserRoles;
import com.example.data.models.entity.TelegramChat;
import com.example.data.models.entity.TelegramUser;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

public class TelegramBotConstants {

    public static final com.example.data.models.entity.User USER_FOR_TESTS = new com.example.data.models.entity.User(
            1L, "testUser", "test@exemple.com", UserRoles.ADMIN.getRole(), true, null, null
    );

    public static final User TELEGRAM_API_USER_FOR_TESTS = new User(12345L, "Elon", false, "Mask",
            "max", "ru",
            null, null, null, null, null);

    public static final TelegramUser TELEGRAM_USER_FOR_TESTS = TelegramUser.builder()
            .id(12345L)
            .username("max")
            .isActive(true)
            .build();

    public static TelegramChat telegramChatForTests = new TelegramChat(
            555L,
            "",
            "",
            "",
            TELEGRAM_USER_FOR_TESTS);

    public static final Chat TELEGRAM_API_CHAT_FOR_TESTS = new Chat(555L, "private");

}
