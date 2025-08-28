package com.example.telegram.bot;

import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.chat.states.UiElements;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.commands.CommandsHandler;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.multimedia.MultimediaHandler;
import com.example.telegram.bot.queries.QueriesHandler;
import com.example.telegram.bot.service.TelegramChatService;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class TelegramBotReactiveHandler {

    private Authentication authentication;
    private final TelegramChatService telegramChatService;
    private final CommandsHandler commandsHandler;
    private final QueriesHandler queriesHandler;
    private final MultimediaHandler multimediaHandler;
    private final TelegramBotMessageSender sender;

    public Mono<Boolean> handleUpdate(Update update) {

        long chatId = UpdateUtilsService.getChatId(update);

        return telegramChatService.getTelegramChatByIdWithTelegramUser(chatId)
                .flatMap(lastTelegramChat -> {
                    if (update.hasMessage()) {
                        return handleMessage(update, lastTelegramChat);
                    } else if (update.hasCallbackQuery()) {
                        log.debug(update.getCallbackQuery().getData());
                        return Mono.just(true);
                    }
                    return Mono.just(false);
                })
                .doOnNext(sent -> log.debug("Сообщение {} отправлено юзеру!",
                        sent ? "" : "не "))
                .doOnTerminate(() -> {
                    log.debug("Конец метода onWebhookUpdateReceived");
                    log.debug("--------------------------------------");
                });
    }

    private Mono<Boolean> handleMessage(Update update, TelegramChat lastChat) {
        Message message = update.getMessage();

        String msgText = message.getText();
        String uiElement = Objects.requireNonNullElse(lastChat.getUiElement(), "");

            if (uiElement.equals(UiElements.COMMAND.getUiElement()) || msgText.startsWith("/")) {
                return commandsHandler.handleCommands(update, lastChat);
            } else if (!msgText.isEmpty()) {
                return queriesHandler.handleQueries(update, lastChat);
            }

            return Mono.just(false);
    }

}
