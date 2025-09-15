package com.example.telegram.constanst;

import com.example.data.models.entity.*;
import com.example.data.models.entity.dto.request.CheckUserRequest;
import com.example.data.models.entity.dto.response.CheckUserResponse;
import com.example.data.models.enums.UserRoles;
import org.springframework.security.core.userdetails.UserDetails;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.time.LocalDateTime;

public class TelegramBotConstants {

    public static final UserDetails USER_DETAILS_WITH_TOURIST_ROLE_FOR_TESTS = org.springframework.security.core.userdetails.User.builder()
            .username("user")
            .password("")
            .roles(UserRoles.TOURIST.getRole())
            .build();

    public static final User USER_FOR_TESTS = new User(
            1L, "testUser", "test@exemple.com", UserRoles.ADMIN.getRole(), true, null, null
    );

    public static final CheckUserRequest CHECK_USER_REQUEST = CheckUserRequest.builder()
            .email(USER_FOR_TESTS.getEmail())
            .build();

    public static final CheckUserResponse CHECK_USER_RESPONSE = CheckUserResponse.builder()
            .found(true)
            .active(true)
            .access_level((byte) 1)
            .build();

    public static final VerificationCode VERIFICATION_CODE_FOR_TESTS = VerificationCode.builder()
            .id(1L)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now())
            .build();

    public static final org.telegram.telegrambots.meta.api.objects.User TELEGRAM_API_USER_FOR_TESTS =
            new org.telegram.telegrambots.meta.api.objects.User(12345L, "Elon", false, "Mask",
            "max", "ru",
            null, null, null, null, null);

    public static final TelegramUser TELEGRAM_USER_FOR_TESTS = TelegramUser.builder()
            .id(12345L)
            .username("max")
            .isActive(true)
            .build();

    public static final TelegramSession TELEGRAM_SESSION_FOR_TESTS = TelegramSession.builder()
            .id(23456L)
            .telegramUser(TELEGRAM_USER_FOR_TESTS)
            .isActive(true)
            .build();

    public static final Integer TELEGRAM_UPDATE_ID = 789456;

    public static TelegramChat telegramChatForTests = new TelegramChat(
            555L,
            1L,
            "",
            "",
            "",
            TELEGRAM_USER_FOR_TESTS);

    public static final Chat TELEGRAM_API_CHAT_FOR_TESTS = new Chat(555L, "private");

}
