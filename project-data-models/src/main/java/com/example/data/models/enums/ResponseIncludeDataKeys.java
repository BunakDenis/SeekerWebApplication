package com.example.data.models.enums;

public enum ResponseIncludeDataKeys {

    USER ("user"),
    USER_DETAILS ("user_details"),

    TELEGRAM_USER ("telegram_user"),

    TELEGRAM_CHAT ("telegram_chat"),

    VERIFICATION_CODE ("verification_code");

    private String includeDataKeyValue;

    ResponseIncludeDataKeys(String keyValue) {this.includeDataKeyValue = keyValue;}

    public String getKeyValue() {
        return includeDataKeyValue;
    }
}
