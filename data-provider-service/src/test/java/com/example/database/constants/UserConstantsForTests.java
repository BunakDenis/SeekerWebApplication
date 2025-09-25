package com.example.database.constants;

import com.example.database.entity.TelegramSession;
import com.example.database.entity.TelegramUser;
import com.example.database.entity.User;
import com.example.data.models.entity.request.CheckUserRequest;
import com.example.data.models.entity.response.CheckUserResponse;
import com.example.data.models.enums.UserRoles;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class UserConstantsForTests {

    public static final UserDetails USER_DETAILS_WITH_TOURIST_ROLE_FOR_TESTS = org.springframework.security.core.userdetails.User.builder()
            .username("testUser")
            .password("")
            .roles(UserRoles.TOURIST.getRole())
            .build();

    public static final com.example.database.entity.UserDetails USER_DETAILS_FOR_TESTS = com.example.database.entity.UserDetails.builder()
            .firstName("Василий")
            .lastname("Тёркин")
            .birthday(LocalDate.of(1980, Month.SEPTEMBER, 20))
            .phoneNumber("+380555555555")
            .gender("MALE")
            .location("Украина, г. Киев")
            .dateStartStudyingSchool(LocalDate.of(2020, Month.FEBRUARY, 11))
            .curator("Руслан Жуковец")
            .build();


    private static final User USER_FOR_TESTS = User.builder()
            .id(1L)
            .username("testUser")
            .password("truthseeker")
            .email("test@exemple.com")
            .role(UserRoles.ADMIN.getRole())
            .isActive(true)
            .userDetails(USER_DETAILS_FOR_TESTS)
            .build();

    public static final CheckUserRequest CHECK_USER_REQUEST = CheckUserRequest.builder()
            .email(USER_FOR_TESTS.getEmail())
            .build();

    public static final CheckUserResponse CHECK_USER_RESPONSE = CheckUserResponse.builder()
            .found(true)
            .active(true)
            .access_level((byte) 1)
            .build();

    public static final TelegramUser TELEGRAM_USER_FOR_TESTS = TelegramUser.builder()
            .telegramUserId(55555L)
            .username("vterk")
            .isActive(true)
            .build();

    public static final TelegramSession TELEGRAM_SESSION_FOR_TESTS = TelegramSession.builder()
            .telegramUser(TELEGRAM_USER_FOR_TESTS)
            .build();

    public static User getUserForTests() {

        User result = User.builder()
                .id(USER_FOR_TESTS.getId())
                .username(USER_FOR_TESTS.getUsername())
                .password(USER_FOR_TESTS.getPassword())
                .email(USER_FOR_TESTS.getEmail())
                .isActive(USER_FOR_TESTS.getIsActive())
                .userDetails(USER_DETAILS_FOR_TESTS)
                .telegramUsers(List.of(TELEGRAM_USER_FOR_TESTS))
                .build();

        return result;
    }

}
