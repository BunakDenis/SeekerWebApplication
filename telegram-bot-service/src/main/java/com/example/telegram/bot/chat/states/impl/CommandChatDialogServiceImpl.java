package com.example.telegram.bot.chat.states.impl;

import com.example.telegram.bot.chat.states.ChatDialogService;
import com.example.telegram.bot.chat.states.UiElements;
import lombok.Data;
import org.springframework.stereotype.Component;


@Data
@Component
public class CommandChatDialogServiceImpl implements ChatDialogService {

    private final String uiElement = UiElements.COMMAND.getUiElement();

    private String command;

    private String dialogState;

    @Override
    public String getUiElement() {
        return uiElement;
    }

    @Override
    public String getUiElementValue() {
        return command;
    }

    @Override
    public String getState() {
        return dialogState;
    }
}
