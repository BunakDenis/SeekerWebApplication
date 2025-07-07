package com.example.telegram.bot;


import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.commands.CommandsHandler;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.multimedia.MultimediaHandler;
import com.example.telegram.bot.queries.QueriesHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.example.telegram.bot.message.MessageProvider.*;

import java.util.List;


@RequiredArgsConstructor
@Log4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${TELEGRAM_BOT_NAME}")
    private String botName;

    @Value("${TELEGRAM_BOT_TOKEN}")
    private String botToken;

    private final TelegramBotMessageSender sender;

    private final CommandsHandler commandsHandler;

    private final QueriesHandler queriesHandler;

    private final MultimediaHandler multimediaHandler;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @PostConstruct
    public void setBotCommands() throws TelegramApiException {
        List<org.telegram.telegrambots.meta.api.objects.commands.BotCommand> commands = List.of(
                new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(Commands.START.getCommand(),
                        "Начать работу с ботом"),
                new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(Commands.AUTHORIZE.getCommand(),
                        "Авторизоваться"),
                new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(Commands.REGISTER.getCommand(),
                        "Зарегистрироваться")
        );
        execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
    }

    @Override
    public void onUpdateReceived(Update update) {

        log.debug("Incoming update - " + update);

        if (update.hasMessage()) {

            Message message = update.getMessage();
            log.debug("incoming message - " + message);

            if (message.hasText()) {
                String msgText = message.getText();

                log.debug("inbox msg text - " + msgText);

                if (msgText.startsWith("/")) {
                    commandsHandler.handleCommands(update);
                } else {
                    queriesHandler.handleQueries(update);
                }

            } else if (message.hasAudio() || message.hasVoice()) {
                log.debug("message.hasAudio() && message.hasVoice()");
                multimediaHandler.handleMultimedia(update);
            } else {
                sender.sendMessage(update.getMessage().getChatId(), UNKNOWN_COMMAND_OR_QUERY);
            }

        } else if (update.hasCallbackQuery()) {
            log.debug(update.getCallbackQuery().getData());
        }
    }
}