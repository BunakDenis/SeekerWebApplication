package com.example.data.models.consts;

import java.text.MessageFormat;

public class WarnMessageProvider {

    private static final String SORRY_MSG = "Извините за неудобства, {0}";
    private static final String NOT_FOUND_USER_WITH_INPUT_EMAIL = "Зарегистрированного юзера с email {0} не найдено, " +
            "введите корректный адрес электронной почты повторно.";
    private static final String NOT_VALID_EMAIL_ADDRESS = "Введённый Вами email адрес {0} не корректный. \n" +
            "Введите Ваш email ещё раз.";
    public static final String NOT_VALID_VERIFICATION_CODE = "Введённый Вами верификационный код не правильный, введите код повторно";
    public static final String EXPIRED_VERIFICATION_CODE = "Срок действия верификационного кода вышел. Для повторной отправки кода введите email адрес.";
    public static final String RE_AUTHORIZATION_MSG = "Для продолжения работы с ботом, пожалуйста повторно пройдите процедуру авторизации";

    public static String getSorryMsg(String msg) {
        return MessageFormat.format(SORRY_MSG, msg);
    }

    public static String getNotValidInputEmailAddress(String email) {
        return MessageFormat.format(NOT_FOUND_USER_WITH_INPUT_EMAIL, email);
    }

    public static String getNotValidEmailAddress(String emailAddress) {
        return MessageFormat.format(NOT_VALID_EMAIL_ADDRESS, emailAddress);
    }

}
