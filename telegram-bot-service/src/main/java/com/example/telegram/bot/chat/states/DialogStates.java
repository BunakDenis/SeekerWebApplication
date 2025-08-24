package com.example.telegram.bot.chat.states;

public enum DialogStates {

    ENTER_EMAIL ("enter_email"),
    EMAIL_VERIFICATION ("email_verification"),
    ENTER_PASSWORD ("enter_password");

    private String dialogState;

    DialogStates(String dialogState) {this.dialogState = dialogState;}

    public String getDialogState() {
        return dialogState;
    }

}
