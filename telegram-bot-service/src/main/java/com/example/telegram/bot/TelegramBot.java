package com.example.telegram.bot;


import com.example.telegram.bot.commands.Commands;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Data
public class TelegramBot extends TelegramWebhookBot {

    @Value("${telegram.bot.name}")
    private String botName;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.webhook-path}")
    private String webhookPath;

    private final TelegramBotReactiveHandler reactiveHandler;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() { // Для webhook
        return webhookPath;
    }

    @PostConstruct
    public void setBotCommands() throws TelegramApiException {
        List<org.telegram.telegrambots.meta.api.objects.commands.BotCommand> commands =
                List.of(
                        new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(Commands.START.getCommand(), "Начать работу с ботом"),
                        new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(Commands.AUTHORIZE.getCommand(), "Авторизоваться"),
                        new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(Commands.REGISTER.getCommand(), "Зарегистрироваться"));
        execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        reactiveHandler.handleUpdate(update).subscribe();
        return null;
    }

}