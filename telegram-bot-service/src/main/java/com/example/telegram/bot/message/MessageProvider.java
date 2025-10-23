package com.example.telegram.bot.message;

import com.example.telegram.bot.commands.Commands;

import java.text.MessageFormat;


public class MessageProvider {


    public static final String START_MSG = "Добро пожаловать в телеграмм бот Искателей!\n\n" +
            "Для регистрации в боте напишите " + Commands.REGISTER.getCommand() + " и пройдите процедуру регистрации.\n\n" +
            "Если Вы являетесь учеником Школы \"Восходящий Поток\" " +
            "и зарегистрированы на <a href=\"https://mystic-school.ru/\">официальном сайте Школы</a>, " +
            "для авторизации в боте используйте те же данные что и на сайте Школы.\n" +
            "Так же если вы зарегистрированы в боте напишите " + Commands.AUTHORIZE.getCommand() +
            " для прохождения процедуры авторизации.\n\n" +
            "Для дальнейшей работы с ботом выберите пункт меню:";

    public static final String EMAIL_CHECKING_MSG = "Введите свой email адрес";
    private static final String EMAIL_VERIFICATION_MSG = "На email адрес {0} было выслано письмо с кодом подтверждения.\n" +
            "Пожалуйста, введите код подтверждения";
    private static final String SUCCESSES_AUTHORIZATION_MSG = "Поздравляю {0}, вы успешно прошли авторизацию!";
    public static final String PASSWORD_CHECKING_MSG = "Введите пароль учётной записи";
    public static final String DATA_VERIFICATION_MSG = "Ожидайте проверки введённых данных";
    public static final String UNKNOWN_COMMAND_OR_QUERY = "К сожалению я такой команды не знаю.";

    public static String getEmailVerificationMsg(String email) {
        return MessageFormat.format(EMAIL_VERIFICATION_MSG, email);
    }
    public static String getSuccessesAuthorizationMsg(String fullName) {
            return MessageFormat.format(SUCCESSES_AUTHORIZATION_MSG, fullName);
    }

}
