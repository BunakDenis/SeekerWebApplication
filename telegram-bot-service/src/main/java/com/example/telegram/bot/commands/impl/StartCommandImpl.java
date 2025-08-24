package com.example.telegram.bot.commands.impl;

import com.example.telegram.bot.chat.states.ChatDialogService;
import com.example.telegram.bot.commands.Command;
import com.example.telegram.bot.entity.TelegramChat;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.queries.Queries;
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
public class StartCommandImpl implements Command {

    @Override
    public Mono<SendMessage> apply(Update update, TelegramChat lastTelegramChat) {

        log.debug("StartCommandImpl метод apply");

        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userFirstName = msg.getFrom().getFirstName();
        String userLastName = msg.getFrom().getLastName();
        StringBuilder greeting = new StringBuilder();

        if (userFirstName != null) {
            greeting.append(userFirstName);
            greeting.append(" ");
        }

        if (userLastName != null) {
            greeting.append(userLastName);
            greeting.append("! ");
        }

        greeting.append(MessageProvider.START_MSG);

        SendMessage answer = new SendMessage(String.valueOf(chatId),
                greeting.toString());

        // Создание reply-кнопки
        KeyboardButton decodeAudioButton = new KeyboardButton(Queries.DECODE_AUDIO.getQuery());

        KeyboardRow row = new KeyboardRow();
        row.add(decodeAudioButton);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(Collections.singletonList(row));
        keyboard.setResizeKeyboard(true);

        answer.setReplyMarkup(keyboard);

        return Mono.just(answer);
    }
}
