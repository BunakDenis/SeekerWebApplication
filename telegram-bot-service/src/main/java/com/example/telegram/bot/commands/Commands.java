package com.example.telegram.bot.commands;


public enum Commands {

    START ("/start"),
    AUTHORIZE ("/authorize"),
    REGISTER("/register");

    private String command;

    Commands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
