package com.example.telegram.bot.message;

import com.example.telegram.bot.commands.Commands;

import java.text.MessageFormat;


public class MessageProvider {


    public static final String START_MSG = "Добро пожаловать в телеграмм бот Искателей!\n\n" +
            Commands.REGISTER.getCommand() + "  -  команда для регистрации в боте.\n\n" +
            Commands.AUTHORIZE.getCommand() + "  -  команда для авторизации в боте. " +
            "Если Вы являетесь учеником Школы \"Восходящий Поток\" " +
            "и зарегистрированы на <a href=\"https://mystic-school.ru/\">официальном сайте Школы</a> или " +
            "зарегистрированы на сайте <a href=\"https://truthseekeroffice.club/web-site/\">кабинета искателя</a> " +
            "для авторизации в боте используйте данные введённые при регистрации на выше перечисленных сайтах.\n\n" +
            "Для дальнейшей работы с ботом выберите пункт меню:";
    public static final String REGISTERED_MSG = "Для прохождения процедуры регистрации перейдите по " +
            "<a href=\"https://truthseekeroffice.club/web-site/register\">ссылке</a>.";
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
