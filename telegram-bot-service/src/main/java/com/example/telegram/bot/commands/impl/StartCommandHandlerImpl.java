package com.example.telegram.bot.commands.impl;

import com.example.telegram.bot.commands.CommandHandler;
import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.keyboard.ReplyKeyboardMarkupProvider;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.queries.Queries;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@Data
@Log4j2
@RequiredArgsConstructor
public class StartCommandHandlerImpl implements CommandHandler {

    @Override
    public Mono<SendMessage> apply(Update update, TelegramChat lastTelegramChat) {

        log.debug("StartCommandImpl метод apply");

        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        StringBuilder greeting = new StringBuilder();

        String telegramUserFullName = UpdateUtilsService.getTelegramUserFullName(update);

        greeting.append(telegramUserFullName);
        greeting.append("! ");

        greeting.append(MessageProvider.START_MSG);

        SendMessage answer = new SendMessage(String.valueOf(chatId),
                greeting.toString());

        // Создание кнопки
        ReplyKeyboardMarkup decodeAudioKeyboard = ReplyKeyboardMarkupProvider.getDecodeAudioKeyboard();

        answer.setReplyMarkup(decodeAudioKeyboard);

        return Mono.just(answer);
    }
}
