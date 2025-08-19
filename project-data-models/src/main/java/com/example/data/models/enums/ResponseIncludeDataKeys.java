package com.example.data.models.enums;

public enum ResponseIncludeDataKeys {

    USER_DETAILS ("user_details"),

    TELEGRAM_USER ("telegram_user");

    private String includeDataKeyValue;

    ResponseIncludeDataKeys(String keyValue) {this.includeDataKeyValue = keyValue;}

    public String getKeyValue() {
        return includeDataKeyValue;
    }
}
