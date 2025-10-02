package com.example.telegram.bot.keyboard;

import com.example.data.models.enums.UserRoles;
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
    public static List<KeyboardRow> getMainMenuKeyboard(UserRoles userRole) {

        List<Queries> queries = List.of(
                Queries.GENERAL_RESULTS, Queries.GOD, Queries.WORK,
                Queries.KNOWLEDGE, Queries.SETTINGS
        );

        List<KeyboardRow> result = createKeyboardList(queries, userRole);

        return new ArrayList<>(result);
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

        return new ArrayList<>(List.of(row1, row2, row3));
    }

    /**
     * Создает клавиатуру для подменю "Работа".
     * @return List of KeyboardRow для меню "Работа".
     */
    public static List<KeyboardRow> getWorkMenuKeyboard() {
        KeyboardButton spiritualBtn = new KeyboardButton(Queries.SPIRITUAL_WORK.getQuery());
        KeyboardButton mysticBtn = new KeyboardButton(Queries.MYSTIC_WORK.getQuery());

        KeyboardRow row1 = new KeyboardRow(List.of(spiritualBtn, mysticBtn));

        return new ArrayList<>(List.of(row1));
    }

    /**
     * Создает клавиатуру для подменю "Духовная Работа".
     * @return List of KeyboardRow для меню "Духовная Работа".
     */
    public static List<KeyboardRow> getSpiritualWorkKeyboard() {

        KeyboardButton purposeBtn = new KeyboardButton(Queries.PURPOSE.getQuery());
        KeyboardButton supplicationBtn = new KeyboardButton(Queries.SUPPLICATION.getQuery());
        KeyboardButton practicesBtn = new KeyboardButton(Queries.PRACTICES.getQuery());
        KeyboardButton ideasBtn = new KeyboardButton(Queries.IDEAS.getQuery());
        KeyboardButton feelingsBtn = new KeyboardButton(Queries.FEELINGS.getQuery());
        KeyboardButton emotionsBtn = new KeyboardButton(Queries.EMOTIONS.getQuery());
        KeyboardButton innerStateDiaryBtn = new KeyboardButton(Queries.INNER_STATE_DIARY.getQuery());
        KeyboardButton contemplationBtn = new KeyboardButton(Queries.CONTEMPLATION.getQuery());
        KeyboardButton remembrancesBtn = new KeyboardButton(Queries.REMEMBRANCES.getQuery());
        KeyboardButton seclusionBtn = new KeyboardButton(Queries.SECLUSION.getQuery());

        KeyboardRow row1 = new KeyboardRow(List.of(purposeBtn, supplicationBtn));
        KeyboardRow row2 = new KeyboardRow(List.of(practicesBtn, ideasBtn));
        KeyboardRow row3 = new KeyboardRow(List.of(feelingsBtn, emotionsBtn));
        KeyboardRow row4 = new KeyboardRow(List.of(innerStateDiaryBtn, contemplationBtn));
        KeyboardRow row5 = new KeyboardRow(List.of(remembrancesBtn, seclusionBtn));

        return new ArrayList<>(List.of(row1, row2, row3, row4, row5));
    }

    /**
     * Создает клавиатуру для подменю "Эмоции".
     * @return List of KeyboardRow для меню "Эмоции".
     */
    public static List<KeyboardRow> getEmotionsKeyboard() {

        KeyboardButton joyBtn = new KeyboardButton(Queries.JOY.getQuery());
        KeyboardButton fearBtn = new KeyboardButton(Queries.FEAR.getQuery());
        KeyboardButton angerBtn = new KeyboardButton(Queries.ANGER.getQuery());
        KeyboardButton sadnessBtn = new KeyboardButton(Queries.SADNESS.getQuery());

        KeyboardRow row1 = new KeyboardRow(List.of(joyBtn, fearBtn));
        KeyboardRow row2 = new KeyboardRow(List.of(angerBtn, sadnessBtn));

        return new ArrayList<>(List.of(row1, row2));
    }

    /**
     * Создает клавиатуру для подменю "Мистическая Работа".
     * @return List of KeyboardRow для меню "Мистическая Работа".
     */
    public static List<KeyboardRow> getMysticWorkKeyboard() {

        KeyboardButton dhikrBtn = new KeyboardButton(Queries.DHIKR.getQuery());
        KeyboardButton prayerBtn = new KeyboardButton(Queries.PRAYER.getQuery());
        KeyboardButton pilgrimageBtn = new KeyboardButton(Queries.PILGRIMAGE.getQuery());
        KeyboardButton healingBtn = new KeyboardButton(Queries.HEALING.getQuery());
        KeyboardButton dreamsBtn = new KeyboardButton(Queries.DREAMS.getQuery());

        KeyboardRow row1 = new KeyboardRow(List.of(dhikrBtn, prayerBtn));
        KeyboardRow row2 = new KeyboardRow(List.of(pilgrimageBtn, healingBtn));
        KeyboardRow row3 = new KeyboardRow(List.of(dreamsBtn));

        return new ArrayList<>(List.of(row1, row2, row3));
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
    private static List<KeyboardRow> createKeyboardList(List<Queries> queries, UserRoles userRole) {

        List<KeyboardButton> availableButtons = new ArrayList<>();

        // Проходим по всем возможным кнопкам
        for (Queries query : Queries.values()) {
            // Добавляем кнопку, только если у пользователя достаточно прав
            if (userRole.hasAccess(query.getRequiredRole())) {
                availableButtons.add(new KeyboardButton(query.getQuery()));
            }
        }

        // Динамически формируем ряды (например, по 2 кнопки в ряду)
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < availableButtons.size(); i += 2) {
            KeyboardRow row = new KeyboardRow();
            row.add(availableButtons.get(i));
            if (i + 1 < availableButtons.size()) {
                row.add(availableButtons.get(i + 1));
            }
            rows.add(row);
        }

        return Collections.unmodifiableList(rows);

    }

}
