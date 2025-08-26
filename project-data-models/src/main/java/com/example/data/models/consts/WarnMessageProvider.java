package com.example.data.models.consts;

import java.text.MessageFormat;

public class WarnMessageProvider {

    private final static String SORRY_MSG = "Извините за неудобства, {0}";

    private final static String NOT_VALID_EMAIL_ADDRESS = "Введённый Вами email адрес {0} не корректный. \n" +
            "Введите Ваш email ещё раз.";

    public static String getSorryMsg(String msg) {
        return MessageFormat.format(SORRY_MSG, msg);
    }

    public static String getNotValidEmailAddress(String emailAddress) {
        return MessageFormat.format(NOT_VALID_EMAIL_ADDRESS, emailAddress);
    }

}
