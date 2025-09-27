package com.example.data.models.enums;

public enum ResponseIncludeDataKeys {

    USER ("user"),
    USER_DETAILS ("user_details"),
    TELEGRAM_USER ("telegram_user"),
    TELEGRAM_CHAT ("telegram_chat"),
    TELEGRAM_SESSION("telegram_session"),
    TRANSIENT_SESSION("transient_session"),
    PERSISTENT_SESSION("persistent_session"),
    VERIFICATION_CODE ("verification_code");

    private String includeDataKeyValue;

    ResponseIncludeDataKeys(String keyValue) {this.includeDataKeyValue = keyValue;}

    public String getKeyValue() {
        return includeDataKeyValue;
    }
}
