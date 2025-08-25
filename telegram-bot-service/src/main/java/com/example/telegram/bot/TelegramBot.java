package com.example.telegram.bot;


import com.example.telegram.bot.chat.states.UiElements;
import com.example.telegram.bot.chat.states.impl.CommandChatDialogServiceImpl;
import com.example.telegram.bot.entity.TelegramChat;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.api.clients.DataProviderClient;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.commands.CommandsHandler;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.multimedia.MultimediaHandler;
import com.example.telegram.bot.queries.QueriesHandler;
import com.example.telegram.bot.service.AuthService;
import com.example.telegram.bot.service.TelegramChatService;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private Authentication authentication;

    private final AuthService authService;

    private final TelegramChatService telegramChatService;
    private final TelegramBotMessageSender sender;
    private final CommandsHandler commandsHandler;
    private final QueriesHandler queriesHandler;
    private final MultimediaHandler multimediaHandler;
    private final CommandChatDialogServiceImpl dialogService;

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
        List<org.telegram.telegrambots.meta.api.objects.commands.BotCommand> commands = List.of(new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(Commands.START.getCommand(), "Начать работу с ботом"), new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(Commands.AUTHORIZE.getCommand(), "Авторизоваться"), new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(Commands.REGISTER.getCommand(), "Зарегистрироваться"));
        execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        long chatId = UpdateUtilsService.getChatId(update);

        log.debug("--------------------------------------");
        log.debug("Начало метода onWebhookUpdateReceived");

        telegramChatService.getTelegramChatByIdWithTelegramUser(chatId)
                .flatMap(lastTelegramChat -> {

                    log.debug("Последний сохранённый чат Юзера {}", lastTelegramChat);
                    log.debug("authentication = {}", authentication);

                    if (Objects.nonNull(update)) {

                        log.debug("Objects.nonNull(update) = true");

                        //Проверка авторизации юзера
                        User telegramApiUser = UpdateUtilsService.getTelegramUser(update);

                        log.debug("Входящее сообщение от Юзера - " + telegramApiUser.getFirstName() + " " + telegramApiUser.getLastName());
                        log.debug("Id юзера - {}", telegramApiUser.getId());

                        if (update.hasMessage()) {

                            Message message = update.getMessage();

                            log.debug("Входящее сообщение - " + message.getText());

                            //Создаём чат для дальнейшей записи в БД
                            TelegramChat currentTelegramChat = TelegramChat.builder()
                                    .id(chatId)
                                    .build();

                            if (message.hasText()) {

                                String msgText = message.getText();

                                String uiElement = "";

                                try {
                                    lastTelegramChat.getUiElement().equals("");
                                    uiElement = lastTelegramChat.getUiElement();
                                } catch (NullPointerException e) {
                                    log.debug("Отсутвует информация о последнем введённом меню.");
                                }

                                if (!uiElement.isEmpty()) {
                                    if (uiElement.equals(UiElements.COMMAND.getUiElement())) {
                                        return commandsHandler.handleCommands(update, lastTelegramChat);
                                    } else if (uiElement.equals(UiElements.QUERY.getUiElement())) {
                                        queriesHandler.handleQueries(update);
                                    }
                                } else if (msgText.startsWith("/")) {
                                    return commandsHandler.handleCommands(update, lastTelegramChat);
                                } else {
                                    queriesHandler.handleQueries(update);
                                }

                            } else if (message.hasAudio() || message.hasVoice()) {
                                log.debug("Юзер прислал медиа файл");
                                multimediaHandler.handleMultimedia(update);
                            } else {
                                sender.sendMessage(update.getMessage().getChatId(),
                                        MessageProvider.UNKNOWN_COMMAND_OR_QUERY);
                                return Mono.just(true);
                            }

                        } else if (update.hasCallbackQuery()) {
                            log.debug(update.getCallbackQuery().getData());
                        }
                    }
                    return Mono.just(false);
                })
                .flatMap(msg -> {

                    log.debug("Сообщение отправлено юзеру!");

                    log.debug("Конец метода onWebhookUpdateReceived");
                    log.debug("--------------------------------------");

                    return Mono.empty();

                })
                .subscribe();

        return null;
    }

}