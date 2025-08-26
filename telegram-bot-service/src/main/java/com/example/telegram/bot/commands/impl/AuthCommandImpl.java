package com.example.telegram.bot.commands.impl;

import com.example.telegram.bot.chat.states.DialogStates;
import com.example.telegram.bot.chat.states.UiElements;
import com.example.telegram.bot.commands.Command;
import com.example.telegram.bot.entity.TelegramChat;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.service.TelegramChatService;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import com.example.utils.sender.EmailService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import reactor.core.publisher.Mono;


@Component
@Data
@Log4j2
@RequiredArgsConstructor
public class AuthCommandImpl implements Command {

    private final TelegramChatService chatService;

    private final EmailService emailService;

    @Override
    public Mono<SendMessage> apply(Update update, TelegramChat chat) {

        log.debug("AuthCommandImpl метод apply");

        log.debug("Последний чат {}", chat);

        User currentUser = UpdateUtilsService.getTelegramUser(update);
        String msgText = UpdateUtilsService.getMessageText(update);
        SendMessage result = new SendMessage();

        result.setChatId(UpdateUtilsService.getChatId(update));

        chat.setUiElement(UiElements.COMMAND.getUiElement());
        chat.setUiElementValue(Commands.AUTHORIZE.getCommand());

        String chatState = chat.getChatState();

        if (chatState.isEmpty()) {

            log.debug("Стадия ввода юзером емейла");

            chat.setChatState(DialogStates.ENTER_EMAIL.getDialogState());
            result.setText(MessageProvider.EMAIL_CHECKING_MSG);

        } else if (msgText.equals(DialogStates.ENTER_EMAIL.getDialogState()) ||
                chatState.equals(DialogStates.ENTER_EMAIL.getDialogState())) {

            try {
                emailService.sendSimpleMail(msgText, "Код верификации", "55555");
                chat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());
                result.setText(MessageProvider.getEmailVerificationMsg(msgText));
            } catch (Exception e) {
                log.debug("Сообщение не отправлено по причине - {}", e.getMessage(), e);
                result.setText("Сообщение не отправлено по причине - " + e.getMessage());
            }

        } else if (msgText.equals(DialogStates.EMAIL_VERIFICATION.getDialogState()) ||
                chatState.equals(DialogStates.EMAIL_VERIFICATION.getDialogState())) {

            log.debug("Стадия проверки введённого юзером кода верификации");

            chat.setUiElement("");
            chat.setUiElementValue("");
            chat.setChatState("");
            result.setText(MessageProvider.getSuccessesAuthorizationMsg(
                    currentUser.getFirstName(), currentUser.getLastName())
            );

        } else {
            result.setText(MessageProvider.UNKNOWN_COMMAND_OR_QUERY);
        }

        /*
            TODO Добавить логику проверки сохранённого чата, если чат не сохранён, написать кастомное сообщение юзеру
         */

        return chatService.save(chat)
                .flatMap(resp -> {
                    log.debug("Сохранённый чат {}", resp);
                    return Mono.just(result);
                });
    }

}
