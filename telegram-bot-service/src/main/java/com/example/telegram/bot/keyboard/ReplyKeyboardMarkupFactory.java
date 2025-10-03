package com.example.telegram.bot.keyboard;

import com.example.data.models.enums.UserRoles;
import com.example.telegram.bot.chat.states.DialogStates;
import com.example.telegram.bot.queries.Queries;
import com.example.utils.collections.CollectionsUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ReplyKeyboardMarkupFactory {

    /**
     * Создает навигационную клавиатуру телеграм бота.
     * <p>Навигационна клавиатура состоит из кнопок:
     * <p>1. Назад (вернуться в предыдущее меню)
     * <p>2. Главное меню
     * <p>3. Избранное (Записи помеченные "флагом" избранное текущей выбранной категории)
     * @return List of KeyboardRow для главного меню.
     */
    public static KeyboardRow getNavigationControlKeyBoard() {

        KeyboardButton returnBtn = new KeyboardButton(Queries.PREV_MENU.getQuery());
        KeyboardButton mainMenuBtn = new KeyboardButton(Queries.MAIN_MENU.getQuery());
        KeyboardButton favBtn = getFavBtn();

        KeyboardRow result = new KeyboardRow(List.of(returnBtn, mainMenuBtn, favBtn));

        return result;
    }

    /**
     * Создает навигационную клавиатуру телеграм бота без кнопки "Избранное".
     * <p>Навигационна клавиатура состоит из кнопок:
     * <p>1. Назад (вернуться в предыдущее меню)
     * <p>2. Главное меню
     * @return List of KeyboardRow для главного меню.
     */
    public static KeyboardRow getNavigationControlKeyBoardWithoutFavBtn() {

        KeyboardButton returnBtn = new KeyboardButton(Queries.PREV_MENU.getQuery());
        KeyboardButton mainMenuBtn = new KeyboardButton(Queries.MAIN_MENU.getQuery());

        KeyboardRow result = new KeyboardRow(List.of(returnBtn, mainMenuBtn));

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

        return CollectionsUtils.cloneListWithStream(result);
    }

    /**
     * Создает клавиатуру для подменю "Господь".
     * @return List of KeyboardRow для меню "Господь".
     */
    public static List<KeyboardRow> getGodMenuKeyboard(UserRoles userRole) {

        ArrayList<KeyboardRow> result = new ArrayList<>();

        List<Queries> queries = List.of(Queries.WILL , Queries.QUESTIONING, Queries.VISION,
                Queries.GOD_MESSAGES, Queries.VOWS);

        List<KeyboardRow> mainMenuKeyboard = getMainMenuKeyboard(userRole);
        List<KeyboardRow> contentKeyboard = createKeyboardList(queries, userRole);

        result.addAll(mainMenuKeyboard);
        result.addAll(contentKeyboard);

        return CollectionsUtils.cloneListWithStream(result);
    }

    /**
     * Создает клавиатуру для подменю "Работа".
     * @return List of KeyboardRow для меню "Работа".
     */
    public static List<KeyboardRow> getWorkMenuKeyboard(UserRoles userRole) {

        List<KeyboardRow> result = createKeyboardList(
                List.of(Queries.SPIRITUAL_WORK, Queries.MYSTIC_WORK),
                userRole
        );

        return CollectionsUtils.cloneListWithStream(result);
    }

    /**
     * Создает клавиатуру для подменю "Духовная Работа".
     * @return List of KeyboardRow для меню "Духовная Работа".
     */
    public static List<KeyboardRow> getSpiritualWorkKeyboard(UserRoles userRole) {

        List<Queries> queries = List.of(
                Queries.PURPOSE, Queries.SUPPLICATION, Queries.PRACTICES, Queries.IDEAS,
                Queries.FEELINGS, Queries.EMOTIONS, Queries.INNER_STATE_DIARY, Queries.CONTEMPLATION,
                Queries.REMEMBRANCES, Queries.SECLUSION
        );

        List<KeyboardRow> mainMenuKeyboard = getMainMenuKeyboard(userRole);
        List<KeyboardRow> contentMenuKeyboard = createKeyboardList(queries, userRole);

        ArrayList<KeyboardRow> result = new ArrayList<>();

        result.addAll(mainMenuKeyboard);
        result.addAll(contentMenuKeyboard);

        return CollectionsUtils.cloneListWithStream(result);
    }

    /**
     * Создает клавиатуру для подменю "Эмоции".
     * @return List of KeyboardRow для меню "Эмоции".
     */
    public static List<KeyboardRow> getEmotionsKeyboard(UserRoles userRole) {

        List<Queries> queries = List.of(
                Queries.JOY, Queries.FEAR, Queries.ANGER, Queries.SADNESS
        );

        List<KeyboardRow> mainMenuKeyboard = getMainMenuKeyboard(userRole);
        List<KeyboardRow> contentMenuKeyboard = createKeyboardList(queries, userRole);

        ArrayList<KeyboardRow> result = new ArrayList<>();

        result.addAll(mainMenuKeyboard);
        result.addAll(contentMenuKeyboard);

        return CollectionsUtils.cloneListWithStream(result);
    }

    /**
     * Создает клавиатуру для подменю "Мистическая Работа".
     * @return List of KeyboardRow для меню "Мистическая Работа".
     */
    public static List<KeyboardRow> getMysticWorkKeyboard(UserRoles userRole) {

        List<Queries> queries = List.of(
                Queries.DHIKR, Queries.PRAYER, Queries.PILGRIMAGE, Queries.HEALING, Queries.DREAMS
        );

        List<KeyboardRow> mainMenuKeyboard = getMainMenuKeyboard(userRole);
        List<KeyboardRow> contentMenuKeyboard = createKeyboardList(queries, userRole);

        ArrayList<KeyboardRow> result = new ArrayList<>();

        result.addAll(mainMenuKeyboard);
        result.addAll(contentMenuKeyboard);

        return CollectionsUtils.cloneListWithStream(result);
    }

    /**
     * Создает клавиатуру для подменю "Знание".
     * @return List of KeyboardRow для меню "Знание".
     */
    public static List<KeyboardRow> getKnowledgeMenuKeyboard(UserRoles userRole) {

        List<Queries> queries = List.of(
                Queries.BOOKS, Queries.AUDIO_BOOKS, Queries.ARTICLE, Queries.MEDIA_LECTURES
        );

        List<KeyboardRow> mainMenuKeyboard = getMainMenuKeyboard(userRole);
        List<KeyboardRow> contentMenuKeyboard = createKeyboardList(queries, userRole);

        ArrayList<KeyboardRow> result = new ArrayList<>();

        result.addAll(mainMenuKeyboard);
        result.addAll(contentMenuKeyboard);

        return CollectionsUtils.cloneListWithStream(result);
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
        for (Queries query : queries) {
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
