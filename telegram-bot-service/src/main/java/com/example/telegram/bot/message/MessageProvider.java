package com.example.telegram.bot.message;

import org.jvnet.hk2.annotations.Service;

@Service
public class MessageProvider {

    public static final String START_MSG = "Добро пожаловать в телеграмм бот для Искателей Истины!\n\n" +
            "На данный момент доступна только функция декодирования аудио.";

    public static final String EMAIL_CHECKING_MSG = "Введите свой email адрес";
    public static final String EMAIL_VERIFICATION_MSG = "На email адрес {} было выслано письмо с кодом подтверждения. " +
            "Пожалуйста, введите код подтверждения";

    public static final String SUCCESSES_AUTHORIZATION_MSG = "Поздравляю {Имя и фамилия юзера}, вы успешно прошли авторизацию!";

    public static final String PASSWORD_CHECKING_MSG = "Введите пароль учётной записи";
    public static final String DATA_VERIFICATION_MSG = "Ожидайте проверки введённых данных";
    public static final String UNKNOWN_COMMAND_OR_QUERY = "К сожалению я такой команды не знаю.";

}
