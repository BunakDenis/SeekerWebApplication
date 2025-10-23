package com.example.telegram.bot.commands;


import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class CommandsHandler {


    private final TelegramBotMessageSender sender;
    private final CommandsHandlersService commandsHandlersService;


    public Mono<Boolean> handleCommands(Update update, TelegramChat lastTelegramChat) {

        log.debug("handleCommands method");

        String command = UpdateUtilsService.getMessageText(update);
        String chatId = UpdateUtilsService.getStringChatId(update);
        String lastCommand = Objects.requireNonNullElse(lastTelegramChat.getUiElementValue(), "");

        log.debug("chat_id = " + chatId + ", command = " + command);
        log.debug("Последний чат {}", lastTelegramChat);

        if (command.equals(Commands.START.getCommand())) {
            log.debug("Выполнение команды start");
        } else if (command.equals(Commands.REGISTER.getCommand())) {
            log.debug("Процедура регистрации юзера в боте.");
        } else if (!lastCommand.isEmpty()) command = lastCommand;

        CommandHandler commandHandler = commandsHandlersService.getCommandHandler(command);

        if (Objects.nonNull(commandHandler)) {

            return commandHandler.apply(update, lastTelegramChat)
                    .flatMap(upds -> {

                        upds.forEach(upd -> sender.sendMessage(upd));

                        return Mono.just(true);
                    });

        }

        sender.sendMessage(new SendMessage(chatId, MessageProvider.UNKNOWN_COMMAND_OR_QUERY));

        return Mono.just(false);
    }
}