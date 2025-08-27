package com.example.telegram.bot.commands;


import com.example.telegram.api.clients.DataProviderClient;
import com.example.telegram.bot.chat.states.impl.CommandChatDialogServiceImpl;
import com.example.telegram.bot.commands.impl.RegCommandImpl;
import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.commands.impl.AuthCommandImpl;
import com.example.telegram.bot.commands.impl.StartCommandImpl;
import com.example.data.models.entity.TelegramUser;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.service.ModelMapperService;
import com.example.telegram.bot.service.TelegramUserService;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Data
@Log4j2
public class CommandsHandler {

    @Autowired
    private TelegramBotMessageSender sender;
    private final Map<String, Command> commands;

    @Autowired
    private DataProviderClient dataProviderClient;
    @Autowired
    private CommandChatDialogServiceImpl dialogService;
    @Autowired
    private TelegramUserService telegramUserService;
    @Autowired
    private ModelMapperService mapperService;

    public CommandsHandler(
            @Autowired StartCommandImpl startCommand,
            @Autowired AuthCommandImpl authCommand,
            @Autowired RegCommandImpl regCommand
    ) {
        this.commands = Map.of(
                Commands.START.getCommand(), startCommand,
                Commands.AUTHORIZE.getCommand(), authCommand,
                Commands.REGISTER.getCommand(), regCommand
        );
    }

    public Mono<Boolean> handleCommands(Update update, TelegramChat lastTelegramChat) {

        log.debug("handleCommands method");

        User telegramUser = UpdateUtilsService.getTelegramUser(update);

        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];

        long chatId = update.getMessage().getChatId();
        String lastCommand = lastTelegramChat.getUiElementValue();

        log.debug("ChatId = " + chatId + ", command = " + command);

        var commandHandler = getCommandHandler(command);

        log.debug(commandHandler);

        if (commandHandler != null) {

            return commandHandler.apply(update, lastTelegramChat)
                            .flatMap(msg -> {
                                sender.sendMessage(msg);
                                return Mono.just(true);
                            });

        } else if (!lastCommand.isEmpty()) {
            commandHandler = getCommandHandler(lastCommand);

            return commandHandler.apply(update, lastTelegramChat)
                    .flatMap(upd -> {
                        sender.sendMessage(upd);
                        return Mono.just(true);
                    });
        } else  {
            sender.sendMessage(new SendMessage(String.valueOf(chatId), MessageProvider.UNKNOWN_COMMAND_OR_QUERY));
        }

        return Mono.just(false);
    }

    private Command getCommandHandler(String command) {
        return commands.get(command);
    }

}
