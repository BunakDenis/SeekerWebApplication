package com.example.data.models.enums;

public enum QueryParameters {

    TELEGRAM_USER_ID ("telegram_user_id");

    private String parameter;

    QueryParameters(String parameterKey) {this.parameter = parameterKey;}

    public String getParameter() {
        return parameter;
    }

}
