package com.example.telegram.bot.queries;

public enum Queries {

    REPEAT_SEND_VERIFICATION_CODE ("Отправить код повторно"),

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
