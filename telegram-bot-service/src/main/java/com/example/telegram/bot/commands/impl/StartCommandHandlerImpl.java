package com.example.telegram.bot.commands.impl;

import com.example.data.models.enums.UserRoles;
import com.example.telegram.bot.chat.UiElements;
import com.example.telegram.bot.commands.CommandHandler;
import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.keyboard.ReplyKeyboardMarkupFactory;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.service.AuthService;
import com.example.telegram.bot.service.TelegramChatService;
import com.example.telegram.bot.service.UserService;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartCommandHandlerImpl implements CommandHandler {

    private final AuthService authService;
    private final UserService userService;
    private final TelegramChatService chatService;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<SendMessage> apply(Update update, TelegramChat lastTelegramChat) {

        log.debug("StartCommandImpl метод apply");

        Long telegramUserId = UpdateUtilsService.getTelegramUserId(update);
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        StringBuilder greeting = new StringBuilder();

        String telegramUserFullName = UpdateUtilsService.getTelegramUserFullName(update);

        greeting.append(telegramUserFullName);
        greeting.append("! ");

        greeting.append(MessageProvider.START_MSG);

        SendMessage greetingAnswer = new SendMessage(String.valueOf(chatId),
                greeting.toString());

        // Указываем, что используем HTML-разметку (включая <a href="...">)
        greetingAnswer.setParseMode("html");

        // Отключаем превью сайта
        greetingAnswer.setDisableWebPagePreview(true);

        return userService.getUserByTelegramUserId(telegramUserId)
                .flatMap(currentUser -> {

                    log.debug("Текущий user = {}", currentUser);

                    UserRoles userRole = UserRoles.valueOf(currentUser.getRole().toUpperCase());

                    List<KeyboardRow> mainMenuKeyboard =
                            ReplyKeyboardMarkupFactory.getMainMenuKeyboard(userRole);
                    KeyboardRow navigationControlKeyBoard = ReplyKeyboardMarkupFactory.getNavigationControlKeyBoard();

                    mainMenuKeyboard.add(0, navigationControlKeyBoard);

                    ReplyKeyboardMarkup replyKeyboard = ReplyKeyboardMarkupFactory.getReplyKeyboard(mainMenuKeyboard);

                    greetingAnswer.setReplyMarkup(replyKeyboard);

                    return Mono.just(greetingAnswer);
                })
                .flatMap(sendMsg -> {
                    TelegramChat tgChatForSave = TelegramChat.builder()
                            .telegramChatId(chatId)
                            .uiElement(UiElements.COMMAND.getUiElement())
                            .uiElementValue(Commands.START.getCommand())
                            .chatState("")
                            .telegramUser(lastTelegramChat.getTelegramUser())
                            .build();

                    return chatService.save(tgChatForSave).then(Mono.just(sendMsg));
                })
                .onErrorResume(err -> {
                    log.error("Ошибка получения текущего User {}", err.getMessage(), err);
                    return Mono.just(new SendMessage(Long.toString(chatId), ""));
                })
                .doFinally(signalType -> log.debug("StartCommandImpl конец метода apply: {}", signalType));
    }
}
