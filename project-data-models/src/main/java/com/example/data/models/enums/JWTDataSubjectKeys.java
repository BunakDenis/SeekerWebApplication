package com.example.data.models.enums;

public enum JWTDataSubjectKeys {

    TELEGRAM_USER_ID ("telegram_user_id");

    private String subjectKey;

    JWTDataSubjectKeys(String subjectKey) {this.subjectKey = subjectKey;}

    public String getSubjectKey() {
        return subjectKey;
    }

}
