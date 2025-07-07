package com.example.telegram.bot.queries;

public enum Queries {

    DECODE_AUDIO ("Декодировать аудио");

    private String query;

    Queries(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
