package com.example.telegram.bot.message;

import java.text.MessageFormat;


public class MessageProvider {

    public static final String START_MSG = "Добро пожаловать в телеграмм бот для Искателей Истины!\n\n" +
            "На данный момент доступна только функция декодирования аудио.";

    public static final String EMAIL_CHECKING_MSG = "Введите свой email адрес";
    public static final String EMAIL_VERIFICATION_MSG = "На email адрес {0} было выслано письмо с кодом подтверждения.\n" +
            "Пожалуйста, введите код подтверждения";
    public static final String SUCCESSES_AUTHORIZATION_MSG = "Поздравляю {0} {1}, вы успешно прошли авторизацию!";

    public static final String PASSWORD_CHECKING_MSG = "Введите пароль учётной записи";
    public static final String DATA_VERIFICATION_MSG = "Ожидайте проверки введённых данных";
    public static final String UNKNOWN_COMMAND_OR_QUERY = "К сожалению я такой команды не знаю.";

    public static String getEmailVerificationMsg(String email) {
        return MessageFormat.format(EMAIL_VERIFICATION_MSG, email);
    }

    public static String getSuccessesAuthorizationMsg(String firstname, String lastname) {
            return MessageFormat.format(SUCCESSES_AUTHORIZATION_MSG, firstname, lastname);
    }

}
