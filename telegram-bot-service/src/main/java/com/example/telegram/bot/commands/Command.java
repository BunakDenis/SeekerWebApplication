package com.example.telegram.bot.commands;


import com.example.telegram.bot.chat.states.ChatDialogService;
import com.example.telegram.bot.entity.TelegramChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

public interface Command {

    Mono<SendMessage> apply(Update update, TelegramChat lastTelegramChat);

}
