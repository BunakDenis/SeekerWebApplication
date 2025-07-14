package com.example.telegram.bot.commands;

import com.example.telegram.bot.message.TelegramBotMessageSender;
import lombok.Data;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.example.telegram.bot.message.MessageProvider.*;

import java.util.Map;

@Component
@Data
@Log4j2
public class CommandsHandler {

    @Autowired
    private TelegramBotMessageSender sender;
    private final Map<String, Command> commands;

    public CommandsHandler(@Autowired StartCommand startCommand) {
        this.commands = Map.of(
                Commands.START.getCommand(), startCommand
        );
    }

    public void handleCommands(Update update) {

        log.debug("handleCommands method");

        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        long chatId = update.getMessage().getChatId();

        log.debug("ChatId = " + chatId + ", command = " + command);

        var commandHandler = commands.get(command);
        if (commandHandler != null) {
            sender.sendMessage(commandHandler.apply(update));
        } else {
            sender.sendMessage(new SendMessage(String.valueOf(chatId), UNKNOWN_COMMAND_OR_QUERY));
        }
    }
}
