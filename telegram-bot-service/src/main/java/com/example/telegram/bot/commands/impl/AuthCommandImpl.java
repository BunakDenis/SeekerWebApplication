package com.example.telegram.bot.commands.impl;

import com.example.telegram.bot.chat.states.ChatDialogService;
import com.example.telegram.bot.chat.states.DialogStates;
import com.example.telegram.bot.chat.states.impl.CommandChatDialogServiceImpl;
import com.example.telegram.bot.commands.Command;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.utils.update.UpdateService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.example.telegram.bot.message.MessageProvider.*;


@Component
@Data
@Log4j2
@RequiredArgsConstructor
public class AuthCommandImpl implements Command {

    private final CommandChatDialogServiceImpl dialogService;

    @Override
    public SendMessage apply(Update update) {

        log.debug("CommandChatDialogServiceImpl метод apply");

        String msgText = UpdateService.getMessageText(update);
        SendMessage result = new SendMessage();

        result.setChatId(UpdateService.getChatId(update));

        if (Commands.AUTHORIZE.getCommand().equals(msgText)) {

            dialogService.setDialogState(DialogStates.ENTER_EMAIL.getDialogState());
            result.setText(EMAIL_CHECKING_MSG);

        } else if (DialogStates.ENTER_EMAIL.getDialogState().equals(msgText)) {
            log.debug("Стадия проверки введённого юзером емейла");
            result.setText(DATA_VERIFICATION_MSG);

            dialogService.setDialogState(null);

        } else {
            result.setText(UNKNOWN_COMMAND_OR_QUERY);
        }
        return result;
    }

    @Override
    public ChatDialogService getChatDialogService() {
        return dialogService;
    }
}
