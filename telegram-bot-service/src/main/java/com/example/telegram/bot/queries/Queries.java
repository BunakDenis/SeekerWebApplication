package com.example.telegram.bot.queries;

public enum Queries {

    //Навигация
    PREV_MENU ("⬅️ Предыдущее меню"),
    MAIN_MENU ("Главное меню"),
    FAVOURITE("⭐ Избранное"),

    //Главное меню
    GENERAL_RESULTS ("Общие результаты"),
    GOD ("Господь"),
    WORK ("Работа"),
    SEEKER ("Искателю"),
    //ADMINISTRATION ("Администрирование"),
    SETTINGS ("Настройки"),

    // Меню "Господь"
    WILL ("Воля"),
    QUESTIONING ("Вопрошание"),
    VISION ("Ведение"),
    GOD_MESSAGES ("Послания"),
    VOWS ("Обеты"),

    // Меню "Работа"
    SPIRITUAL_WORK ("Духовная Работа"),
    MYSTIC_WORK ("Мистическая Работа"),

    // Меню "Знание"
    BOOKS ("Книги"),
    AUDIO_BOOKS ("Аудиокниги"),
    ARTICLE ("Статьи"),
    MEDIA_LECTURES ("Аудио-видео лекции"),

    DECODE_AUDIO ("Декодировать аудио"),

    GET_ACTUAL_USEFUL_TOOLS_HEALS ("useful tools heals");

    private String query;

    Queries(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
