package com.example.telegram.constanst;

import com.example.data.models.entity.VerificationCode;
import com.example.data.models.enums.UserRoles;
import com.example.data.models.entity.TelegramChat;
import com.example.data.models.entity.TelegramUser;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

public class TelegramBotConstants {

    public static final com.example.data.models.entity.User USER_FOR_TESTS = new com.example.data.models.entity.User(
            1L, "testUser", "test@exemple.com", UserRoles.ADMIN.getRole(), true, null, null
    );

    public static final VerificationCode VERIFICATION_CODE_FOR_TESTS = VerificationCode.builder()
            .id(1L)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(5L))
            .build();

    public static final User TELEGRAM_API_USER_FOR_TESTS = new User(12345L, "Elon", false, "Mask",
            "max", "ru",
            null, null, null, null, null);

    public static final TelegramUser TELEGRAM_USER_FOR_TESTS = TelegramUser.builder()
            .id(12345L)
            .username("max")
            .isActive(true)
            .build();

    public static final Integer TELEGRAM_UPDATE_ID = 789456;

    public static TelegramChat telegramChatForTests = new TelegramChat(
            555L,
            "",
            "",
            "",
            TELEGRAM_USER_FOR_TESTS);

    public static final Chat TELEGRAM_API_CHAT_FOR_TESTS = new Chat(555L, "private");

}
