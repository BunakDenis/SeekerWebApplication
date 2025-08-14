package com.example.telegram.bot.commands;


import com.example.telegram.bot.chat.states.ChatDialogService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {

    SendMessage apply(Update update);

    ChatDialogService getChatDialogService();

}
