package com.example.telegram.bot.keyboard;

import com.example.telegram.bot.queries.Queries;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Collections;

@Component
public class ReplyKeyboardMarkupProvider {

    public static ReplyKeyboardMarkup getNotValidEmailKeyboard() {
        KeyboardButton repeatedSendVerificationCodeBtn = new KeyboardButton(Queries.REPEAT_SEND_VERIFICATION_CODE.getQuery());

        KeyboardRow row = new KeyboardRow();
        row.add(repeatedSendVerificationCodeBtn);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(Collections.singletonList(row));
        keyboard.setResizeKeyboard(true);

       return keyboard;
    }
    public static ReplyKeyboardMarkup getDecodeAudioKeyboard() {

        KeyboardButton decodeAudioButton = new KeyboardButton(Queries.DECODE_AUDIO.getQuery());

        KeyboardRow row = new KeyboardRow();
        row.add(decodeAudioButton);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(Collections.singletonList(row));
        keyboard.setResizeKeyboard(true);

        return keyboard;
    }

}
