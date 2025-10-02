package com.example.telegram.bot.queries;

import com.example.data.models.enums.UserRoles;

public enum Queries {

    //Навигация
    PREV_MENU ("⬅️ Предыдущее меню", UserRoles.TOURIST),
    MAIN_MENU ("Главное меню", UserRoles.TOURIST),
    FAVOURITE("⭐ Избранное", UserRoles.TOURIST),

    //Главное меню
    GENERAL_RESULTS ("Общие результаты", UserRoles.TOURIST),
    GOD ("Господь", UserRoles.USER),
    WORK ("Работа", UserRoles.TOURIST),
    KNOWLEDGE ("Знание", UserRoles.TOURIST),
    //ADMINISTRATION ("Администрирование"),
    SETTINGS ("Настройки", UserRoles.TOURIST),

    // Меню "Господь"
    WILL ("Воля", UserRoles.USER),
    QUESTIONING ("Вопрошание", UserRoles.USER),
    VISION ("Ведение", UserRoles.USER),
    GOD_MESSAGES ("Послания", UserRoles.ADVANCED),
    VOWS ("Обеты", UserRoles.USER),

    // Меню "Работа"
    SPIRITUAL_WORK ("Духовная Работа", UserRoles.TOURIST),
    MYSTIC_WORK ("Мистическая Работа", UserRoles.TOURIST),

    //Подменю "Духовная Работа"
    PURPOSE ("Цели", UserRoles.TOURIST),
    SUPPLICATION ("Запрос", UserRoles.TOURIST),
    PRACTICES ("Практики", UserRoles.TOURIST),
    IDEAS ("Идеи", UserRoles.TOURIST),
    FEELINGS ("Чувства", UserRoles.TOURIST),
    DESIRES ("Желания", UserRoles.TOURIST),
    EMOTIONS ("Эмоции", UserRoles.TOURIST),
    INNER_STATE_DIARY ("Дневник состояний", UserRoles.TOURIST),
    CONTEMPLATION ("Созерцание", UserRoles.TOURIST),
    REMEMBRANCES ("Воспоминания", UserRoles.TOURIST),
    SECLUSION ("Затвор", UserRoles.TOURIST),

    //Подменю "Эмоции"
    JOY ("Радость", UserRoles.TOURIST),
    FEAR ("Страх", UserRoles.TOURIST),
    ANGER ("Гнев", UserRoles.TOURIST),
    SADNESS ("Печаль", UserRoles.TOURIST),

    //Подменю "Мистическая Работа"
    DHIKR ("Зикр", UserRoles.TOURIST),
    PRAYER ("Молитва", UserRoles.TOURIST),
    PILGRIMAGE ("Паломничество", UserRoles.ADVANCED),
    HEALING ("Целительство", UserRoles.ADVANCED),
    DREAMS ("Сновидения", UserRoles.TOURIST),

    // Меню "Знание"
    BOOKS ("Книги", UserRoles.TOURIST),
    AUDIO_BOOKS ("Аудиокниги", UserRoles.TOURIST),
    ARTICLE ("Статьи", UserRoles.TOURIST),
    MEDIA_LECTURES ("Аудио-видео лекции", UserRoles.TOURIST),

    DECODE_AUDIO ("Декодировать аудио", UserRoles.TOURIST),

    GET_ACTUAL_USEFUL_TOOLS_HEALS ("useful tools heals", UserRoles.ADVANCED);

    private String query;
    private UserRoles requiredRole;

    Queries(String query, UserRoles requiredRole) {
        this.query = query;
        this.requiredRole = requiredRole;
    }

    public String getQuery() {
        return query;
    }

    public UserRoles getRequiredRole() {
        return requiredRole;
    }
}
