package com.example.telegram.bot;

import com.example.data.models.consts.WarnMessageProvider;
import com.example.data.models.entity.TelegramChat;
import com.example.data.models.service.ModelMapperService;
import com.example.telegram.bot.chat.UiElements;
import com.example.telegram.bot.commands.CommandsHandler;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.multimedia.MultimediaHandler;
import com.example.telegram.bot.queries.QueriesHandler;
import com.example.telegram.bot.service.*;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class TelegramBotReactiveHandler {

    private Authentication authentication;
    private final AuthService authService;
    private final UserService userService;
    private final TelegramUserService telegramUserService;
    private final TelegramChatService telegramChatService;
    private final CommandsHandler commandsHandler;
    private final QueriesHandler queriesHandler;
    private final MultimediaHandler multimediaHandler;
    private final TelegramBotMessageSender sender;
    private final ModelMapperService mapperService;

    public Mono<Boolean> handleUpdate(Update update) {

        log.info("Метод handleUpdate");

        User telegramApiUser = UpdateUtilsService.getTelegramUser(update);
        Long telegramUserId = UpdateUtilsService.getTelegramUserId(update);
        long chatId = UpdateUtilsService.getChatId(update);

        return telegramChatService.getTelegramChatByIdWithTgUser(chatId)
                .switchIfEmpty(
                        authService.registeredAsDefaultUserIfNotExists(update)
                                .flatMap(tgUser -> {

                                    TelegramChat chatForSave = TelegramChat.builder()
                                            .telegramChatId(chatId)
                                            .uiElement("")
                                            .uiElementValue("")
                                            .chatState("")
                                            .telegramUser(tgUser)
                                            .build();

                                    return telegramChatService.save(chatForSave);

                                })
                )
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
                // Ловим любые ошибки в цепочке
                .doOnError(ex -> log.error("Ошибка обработки update:", ex))
                .onErrorResume(ex -> {
                    // При ошибке отправляем пользователю «sorryMsg»
                    String text = WarnMessageProvider.getSorryMsg(
                            "бот временно недоступен, попробуйте обратится к боту позже"
                    );
                    SendMessage sorry = new SendMessage(String.valueOf(chatId), text);
                    sender.sendMessage(sorry);
                    return Mono.just(false);
                })
                .doFinally(signalType -> log.debug("Конец метода handleUpdate: {}", signalType));
    }

    private Mono<Boolean> handleMessage(Update update, TelegramChat lastChat) {

        log.info("Метод handleMessage");

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
