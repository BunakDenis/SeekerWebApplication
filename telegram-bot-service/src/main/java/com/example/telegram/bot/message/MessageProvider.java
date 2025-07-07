package com.example.telegram.bot.message;

import org.jvnet.hk2.annotations.Service;

@Service
public class MessageProvider {

    public static final String START_MSG = "Добро пожаловать в телеграмм бот для Искателей Истины!\n\n" +
            "На данный момент доступна только функция декодирования аудио.";

    public static final String UNKNOWN_COMMAND_OR_QUERY = "К сожалению я такой команды не знаю.";

}
