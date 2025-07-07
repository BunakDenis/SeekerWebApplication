package com.example.telegram.bot.commands;

import com.example.telegram.bot.queries.Queries;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import static com.example.telegram.bot.message.MessageProvider.*;

import java.util.Collections;

@Component
@Data
@RequiredArgsConstructor
public class StartCommand implements Command {

    @Override
    public SendMessage apply(Update update) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();

        SendMessage answer = new SendMessage(String.valueOf(chatId),
                msg.getFrom().getFirstName() + " " + msg.getFrom().getLastName() + "! " + START_MSG);

        // Создание reply-кнопки
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(Queries.DECODE_AUDIO.getQuery()));
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(Collections.singletonList(row));
        keyboard.setResizeKeyboard(true);

        answer.setReplyMarkup(keyboard);

        return answer;
    }
}
