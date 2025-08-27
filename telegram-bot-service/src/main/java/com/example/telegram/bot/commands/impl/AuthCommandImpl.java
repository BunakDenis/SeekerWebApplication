package com.example.telegram.bot.commands.impl;


import com.example.data.models.entity.VerificationCode;
import com.example.telegram.bot.chat.states.DialogStates;
import com.example.telegram.bot.chat.states.UiElements;
import com.example.telegram.bot.commands.Command;
import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.service.TelegramChatService;
import com.example.telegram.bot.service.UserService;
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

    private final UserService userService;
    private final TelegramChatService chatService;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;

    @Override
    public Mono<SendMessage> apply(Update update, TelegramChat chat) {
        return Mono.just("")
                .flatMap(resp -> {
                    log.info("AuthCommandImpl метод apply");

                    log.debug("Последний чат {}", chat);

                    String msgText = UpdateUtilsService.getMessageText(update);

                    chat.setUiElement(UiElements.COMMAND.getUiElement());
                    chat.setUiElementValue(Commands.AUTHORIZE.getCommand());

                    String chatState = chat.getChatState();

                    if (chatState.isEmpty()) {

                        return emailInputtingStateHandler(update, chat);

                    } else if (msgText.equals(DialogStates.ENTER_EMAIL.getDialogState()) ||
                            chatState.equals(DialogStates.ENTER_EMAIL.getDialogState())) {

                        return emailCheckingStateHandler(update, chat);

                    } else if (msgText.equals(DialogStates.EMAIL_VERIFICATION.getDialogState()) ||
                            chatState.equals(DialogStates.EMAIL_VERIFICATION.getDialogState())) {

                        return verificationCodeValidatingStateHandler(update, chat);

                    }

                    return Mono.just(
                            new SendMessage(
                                    UpdateUtilsService.getStringChatId(update),
                                    MessageProvider.UNKNOWN_COMMAND_OR_QUERY
                            )
                    );
                })
                .flatMap(msg -> chatService.save(chat)
                        .flatMap(resp -> {
                            log.debug("Сохранённый чат {}", resp);
                            return Mono.just(msg);
                        })
                );
    }

    private Mono<SendMessage> emailInputtingStateHandler(Update update, TelegramChat chat) {
        return Mono.just("")
                .flatMap(some -> {

                    log.info("Стадия ввода юзером емейла");

                    chat.setChatState(DialogStates.ENTER_EMAIL.getDialogState());

                    return Mono.just(
                            new SendMessage(
                                    UpdateUtilsService.getStringChatId(update),
                                    MessageProvider.EMAIL_CHECKING_MSG
                            )
                    );
                });

    }

    private Mono<SendMessage> emailCheckingStateHandler(Update update, TelegramChat chat) {

        log.info("Стадия валидации емейла и отправки сообщения для его верификации");

        Long telegramUserId = UpdateUtilsService.getTelegramUserId(update);

        String msgText = UpdateUtilsService.getMessageText(update);

        SendMessage result = new SendMessage();
        result.setChatId(UpdateUtilsService.getChatId(update));

        return Mono.just(result)
                .flatMap(msg -> {

                    log.debug(
                            "emailService.isEmailAddressValid(msgText) = {}",
                            emailService.isEmailAddressValid(msgText)
                    );

                    try {
                        if (emailService.isEmailAddressValid(msgText)) {
                            log.debug("Email {} is valid", msgText);
                            String verificationCode = GenerationService.generateEmailVerificationCode();

                            emailService.sendSimpleMail(
                                    msgText,
                                    "Код верификации в " + botName,
                                    "Ваш верификационный код - " + verificationCode
                            );
                            return Mono.just(verificationCode);
                        }

                        result.setText(getNotValidEmailAddress(msgText));
                        return Mono.just("");

                    } catch (Exception e) {
                        log.error("Сообщение не отправлено по причине - {}", e.getMessage(), e);
                        result.setText(getSorryMsg("на данный момент нет возможности отправить письмо " +
                                "для верификации вашей электронной почты, попробуйте пройти верификацию позже."));

                        return Mono.just("");
                    }
                })
                .flatMap(code -> {
                    if (!code.isEmpty()) {

                        chat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());
                        result.setText(MessageProvider.getEmailVerificationMsg(msgText));

                        return userService.getUserByTelegramUserId(telegramUserId)
                                .flatMap(user ->
                                    verificationCodeService.save(
                                            VerificationCode.builder()
                                                    .otpHash(code)
                                                    .user(user)
                                                    .build())
                                )
                                .flatMap(verificationCode -> Mono.just(verificationCode.getOtpHash()));
                    }
                    return Mono.just("");
                })
                .flatMap(code -> {
                    log.debug("Verification code перед отправкой сообщения {}", code);
                    log.debug("SendMessage = {}", result);
                    return Mono.just(result);
                });

    }

    private Mono<SendMessage> verificationCodeValidatingStateHandler(Update update, TelegramChat chat) {

        log.info("Стадия проверки введённого юзером кода верификации");

        User currentUser = UpdateUtilsService.getTelegramUser(update);

        chat.setUiElement("");
        chat.setUiElementValue("");
        chat.setChatState("");

        return Mono.just(
                new SendMessage(
                        UpdateUtilsService.getStringChatId(update),
                        MessageProvider.getSuccessesAuthorizationMsg(
                                currentUser.getFirstName(), currentUser.getLastName())
                )
        );
    }

}
