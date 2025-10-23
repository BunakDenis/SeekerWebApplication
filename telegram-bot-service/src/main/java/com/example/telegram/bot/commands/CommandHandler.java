package com.example.telegram.bot.commands;


import com.example.data.models.entity.TelegramChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CommandHandler {

    Mono<List<SendMessage>> apply(Update update, TelegramChat lastTelegramChat);

}
