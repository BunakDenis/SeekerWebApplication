package com.example.telegram.bot.commands;


import com.example.telegram.api.clients.DataProviderClient;
import com.example.telegram.bot.chat.states.ChatDialogService;
import com.example.telegram.bot.chat.states.DialogStates;
import com.example.telegram.bot.chat.states.UiElements;
import com.example.telegram.bot.chat.states.impl.CommandChatDialogServiceImpl;
import com.example.telegram.bot.commands.impl.AuthCommandImpl;
import com.example.telegram.bot.commands.impl.RegCommandImpl;
import com.example.telegram.bot.commands.impl.StartCommandImpl;
import com.example.telegram.bot.entity.TelegramChat;
import com.example.telegram.bot.entity.TelegramUser;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.service.TelegramUserService;
import com.example.telegram.bot.utils.update.UpdateService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.example.telegram.bot.message.MessageProvider.*;

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
    TelegramUserService telegramUserService;

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

    public CommandChatDialogServiceImpl handleCommands(Update update) {

        log.debug("handleCommands method");

        User telegramUser = UpdateService.getTelegramUser(update);

        TelegramUser telegramUserForCheck = telegramUserService.apiTelegramUserToEntity(telegramUser);

        String email = "xisi926@ukr.net";

        dataProviderClient.checkTelegramUserAuthentication(telegramUserForCheck.getId())
                .subscribe(resp -> {
                    log.debug(resp);
                });

        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        long chatId = update.getMessage().getChatId();

        log.debug("ChatId = " + chatId + ", command = " + command);

        var commandHandler = getCommandHandler(command);

        if (commandHandler != null) {

            sender.sendMessage(commandHandler.apply(update));

            TelegramChat chatForSave = TelegramChat.builder()
                    .id(chatId)
                    .uiElement(UiElements.COMMAND.getUiElement())
                    .uiElementValue(command)
                    .telegramUser(telegramUserForCheck)
                    .build();

            /*
            Mono<ApiResponse<TelegramChatDTO>> saveTelegramChat = dataProviderClient.saveTelegramChat(chatForSave);

            saveTelegramChat.doOnSuccess(resp -> {
                log.debug("Chat saved successfully {}", resp);
            })
                    .doOnError(err -> log.debug("Chat don't saved {}", err));

            Mono<ApiResponse<TelegramChatDTO>> telegramChats = dataProviderClient.getTelegramChats(chatId);

            telegramChats.doOnSuccess(resp -> {
                log.debug("All user " + telegramUser + " chats " + resp.getData());
            });
*/
        } else  {
            sender.sendMessage(new SendMessage(String.valueOf(chatId), UNKNOWN_COMMAND_OR_QUERY));
        }

        return dialogService;
    }

    private Command getCommandHandler(String command) {
        return commands.get(command);
    }

}
