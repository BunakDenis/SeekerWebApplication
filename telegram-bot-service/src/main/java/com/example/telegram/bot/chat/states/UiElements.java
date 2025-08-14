package com.example.telegram.bot.chat.states;

public enum UiElements {

    COMMAND ("command"),
    QUERY ("query"),
    REPLAY_BUTTON ("replay_button"),
    INLINE_BUTTON ("inline_button");

    private String uiElement;

    UiElements (String element) {this.uiElement = element;}

    public String getUiElement() {
        return uiElement;
    }

}
