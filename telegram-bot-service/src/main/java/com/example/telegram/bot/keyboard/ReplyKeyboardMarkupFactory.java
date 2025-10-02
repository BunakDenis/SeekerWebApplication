package com.example.telegram.bot.keyboard;

import com.example.telegram.bot.chat.states.DialogStates;
import com.example.telegram.bot.queries.Queries;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ReplyKeyboardMarkupFactory {

    public static KeyboardRow getNavigationControlKeyBoard() {

        KeyboardButton returnBtn = new KeyboardButton(Queries.PREV_MENU.getQuery());
        KeyboardButton mainMenuBtn = new KeyboardButton(Queries.MAIN_MENU.getQuery());
        KeyboardButton favBtn = getFavBtn();

        KeyboardRow result = new KeyboardRow(List.of(returnBtn, mainMenuBtn, favBtn));

        return result;
    }

    /**
     * Создает клавиатуру главного меню (первый уровень).
     * @return List of KeyboardRow для главного меню.
     */
    public static List<KeyboardRow> getMainMenuKeyboard() {
        KeyboardButton generalBtn = new KeyboardButton(Queries.GENERAL_RESULTS.getQuery());
        KeyboardButton godBtn = new KeyboardButton(Queries.GOD.getQuery());
        KeyboardButton workBtn = new KeyboardButton(Queries.WORK.getQuery());
        KeyboardButton seekerBtn = new KeyboardButton(Queries.SEEKER.getQuery());
        KeyboardButton settingsBtn = new KeyboardButton(Queries.SETTINGS.getQuery());

        KeyboardRow row1 = new KeyboardRow(List.of(generalBtn, godBtn));
        KeyboardRow row2 = new KeyboardRow(List.of(workBtn, seekerBtn));
        KeyboardRow row3 = new KeyboardRow(List.of(settingsBtn));

        return new ArrayList<>(List.of(row1, row2, row3));
    }

    /**
     * Создает клавиатуру для подменю "Господь".
     * @return List of KeyboardRow для меню "Господь".
     */
    public static List<KeyboardRow> getGodMenuKeyboard() {
        KeyboardButton willBtn = new KeyboardButton(Queries.WILL.getQuery());
        KeyboardButton questioningBtn = new KeyboardButton(Queries.QUESTIONING.getQuery());
        KeyboardButton visionBtn = new KeyboardButton(Queries.VISION.getQuery());
        KeyboardButton messageBtn = new KeyboardButton(Queries.GOD_MESSAGES.getQuery());
        KeyboardButton vowBtn = new KeyboardButton(Queries.VOWS.getQuery());

        KeyboardRow row1 = new KeyboardRow(List.of(willBtn, questioningBtn));
        KeyboardRow row2 = new KeyboardRow(List.of(visionBtn, messageBtn));
        KeyboardRow row3 = new KeyboardRow(List.of(vowBtn));

        return List.of(row1, row2, row3);
    }

    /**
     * Создает клавиатуру для подменю "Работа".
     * @return List of KeyboardRow для меню "Работа".
     */
    public static List<KeyboardRow> getWorkMenuKeyboard() {
        KeyboardButton spiritualBtn = new KeyboardButton(Queries.SPIRITUAL_WORK.getQuery());
        KeyboardButton mysticBtn = new KeyboardButton(Queries.MYSTIC_WORK.getQuery());

        KeyboardRow row1 = new KeyboardRow(List.of(spiritualBtn, mysticBtn));

        return List.of(row1);
    }

    /**
     * Создает клавиатуру для подменю "Знание".
     * @return List of KeyboardRow для меню "Знание".
     */
    public static List<KeyboardRow> getSeekerMenuKeyboard() {
        KeyboardButton booksBtn = new KeyboardButton(Queries.BOOKS.getQuery());
        KeyboardButton audioBooksBtn = new KeyboardButton(Queries.AUDIO_BOOKS.getQuery());
        KeyboardButton articleBtn = new KeyboardButton(Queries.ARTICLE.getQuery());
        KeyboardButton lecturesBtn = new KeyboardButton(Queries.MEDIA_LECTURES.getQuery());

        KeyboardRow row1 = new KeyboardRow(List.of(booksBtn, audioBooksBtn));
        KeyboardRow row2 = new KeyboardRow(List.of(articleBtn, lecturesBtn));

        return List.of(row1, row2);
    }
    private static KeyboardButton getFavBtn() {
        return new KeyboardButton(Queries.FAVOURITE.getQuery());
    }
    public static KeyboardRow getFavBtnRow() {
        return new KeyboardRow(List.of(getFavBtn()));
    }
    public static KeyboardRow getNotValidEmailKeyboard() {
        KeyboardButton repeatedSendVerificationCodeBtn =
                new KeyboardButton(DialogStates.REPEAT_SEND_VERIFICATION_CODE.getDialogState());

        KeyboardRow result = new KeyboardRow();
        result.add(repeatedSendVerificationCodeBtn);

       return result;
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
    public static ReplyKeyboardMarkup getReplyKeyboard(List<KeyboardRow> keyboardRows) {
        ReplyKeyboardMarkup result = new ReplyKeyboardMarkup();
        result.setKeyboard(keyboardRows);
        result.setResizeKeyboard(false);
        result.setOneTimeKeyboard(false);
        result.setSelective(true);

        return result;
    }
    public static ReplyKeyboardMarkup getEmptyReplyKeyboard() {

        ReplyKeyboardMarkup result = new ReplyKeyboardMarkup();
        result.setResizeKeyboard(false);
        result.setOneTimeKeyboard(false);
        result.setSelective(true);

        return result;
    }

}
