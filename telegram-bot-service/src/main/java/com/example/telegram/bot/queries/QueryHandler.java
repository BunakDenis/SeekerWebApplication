package com.example.telegram.bot.queries;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface QueryHandler {

    SendMessage apply(Update update);

}
