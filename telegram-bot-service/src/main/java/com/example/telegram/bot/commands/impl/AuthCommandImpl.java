package com.example.telegram.bot.commands.impl;


import com.example.data.models.entity.VerificationCode;
import com.example.telegram.bot.chat.states.DialogStates;
import com.example.telegram.bot.chat.states.UiElements;
import com.example.telegram.bot.commands.Command;
import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.service.TelegramChatService;
import com.example.telegram.bot.service.VerificationCodeService;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import com.example.utils.generator.GenerationService;
import com.example.utils.sender.EmailService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import reactor.core.publisher.Mono;

import static com.example.data.models.consts.WarnMessageProvider.*;


@Component
@Data
@Log4j2
@RequiredArgsConstructor
public class AuthCommandImpl implements Command {

    @Value("${telegram.bot.name}")
    private String botName;
    private final TelegramChatService chatService;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;

    @Override
    public Mono<SendMessage> apply(Update update, TelegramChat chat) {

        log.info("AuthCommandImpl метод apply");

        log.debug("Последний чат {}", chat);

        User currentUser = UpdateUtilsService.getTelegramUser(update);
        String msgText = UpdateUtilsService.getMessageText(update);
        SendMessage result = new SendMessage();

        result.setChatId(UpdateUtilsService.getChatId(update));

        chat.setUiElement(UiElements.COMMAND.getUiElement());
        chat.setUiElementValue(Commands.AUTHORIZE.getCommand());

        String chatState = chat.getChatState();

        if (chatState.isEmpty()) {

            log.info("Стадия ввода юзером емейла");

            chat.setChatState(DialogStates.ENTER_EMAIL.getDialogState());
            result.setText(MessageProvider.EMAIL_CHECKING_MSG);

        } else if (msgText.equals(DialogStates.ENTER_EMAIL.getDialogState()) ||
                chatState.equals(DialogStates.ENTER_EMAIL.getDialogState())) {

            try {
                if (emailService.isEmailAddressValid(msgText)) {

                    String verificationCode = GenerationService.generateEmailVerificationCode();

                    emailService.sendSimpleMail(
                            msgText,
                            "Код верификации в " + botName,
                            "Ваш верификационный код - " + verificationCode
                    );

                    verificationCodeService.save(
                            VerificationCode.builder()
                                    .otpHash(verificationCode)
                                    .build()
                    );

                    chat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());
                    result.setText(MessageProvider.getEmailVerificationMsg(msgText));
                } else {
                    result.setText(getNotValidEmailAddress(msgText));
                }
            } catch (Exception e) {
                log.error("Сообщение не отправлено по причине - {}", e.getMessage(), e);
                result.setText(getSorryMsg("на данный момент нет возможности отправить письмо " +
                        "для верификации вашей электронной почты, попробуйте пройти верификацию позже."));
            }

        } else if (msgText.equals(DialogStates.EMAIL_VERIFICATION.getDialogState()) ||
                chatState.equals(DialogStates.EMAIL_VERIFICATION.getDialogState())) {

            log.info("Стадия проверки введённого юзером кода верификации");

            chat.setUiElement("");
            chat.setUiElementValue("");
            chat.setChatState("");
            result.setText(MessageProvider.getSuccessesAuthorizationMsg(
                    currentUser.getFirstName(), currentUser.getLastName())
            );

        } else {
            result.setText(MessageProvider.UNKNOWN_COMMAND_OR_QUERY);
        }

        return chatService.save(chat)
                .flatMap(resp -> {
                    log.debug("Сохранённый чат {}", resp);
                    return Mono.just(result);
                });
    }

}
