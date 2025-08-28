package com.example.telegram.bot.commands.impl;


import com.example.data.models.entity.VerificationCode;
import com.example.telegram.bot.chat.UiElements;
import com.example.telegram.bot.chat.states.DialogStates;
import com.example.telegram.bot.commands.CommandHandler;
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
import reactor.core.scheduler.Schedulers;

import static com.example.data.models.consts.WarnMessageProvider.*;


@Component
@Data
@Log4j2
@RequiredArgsConstructor
public class AuthCommandHandlerImpl implements CommandHandler {

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
        final long chatId = UpdateUtilsService.getChatId(update);
        final Long tgUserId = UpdateUtilsService.getTelegramUserId(update);
        final String email = UpdateUtilsService.getMessageText(update);

        return Mono.defer(() -> {
            // 1) Валидация email — синхронно -> map / just
            if (!emailService.isEmailAddressValid(email)) {
                return Mono.just(new SendMessage(String.valueOf(chatId), getNotValidEmailAddress(email)));
            }

            // 2) Генерация и подготовка сообщений
            final String code = GenerationService.generateEmailVerificationCode();

            chat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());
            final SendMessage okMsg = new SendMessage(String.valueOf(chatId), MessageProvider.getEmailVerificationMsg(email));
            final SendMessage sorryMsg = new SendMessage(String.valueOf(chatId),
                    getSorryMsg("на данный момент нет возможности отправить письмо " +
                            "для верификации вашей электронной почты, попробуйте пройти верификацию позже."));

            // 3) Отправка письма (side-effect) + сохранение кода + возврат итогового сообщения
            return Mono.fromRunnable(() ->
                            emailService.sendSimpleMail(
                                    email,
                                    "Код верификации в " + botName,
                                    "Ваш верификационный код - " + code
                            )
                    )
                    .subscribeOn(Schedulers.boundedElastic())
                    .then( // дождаться отправки письма
                            userService.getUserByTelegramUserId(tgUserId)
                                    .switchIfEmpty(Mono.error(new IllegalStateException(
                                            "User not found for telegramUserId=" + tgUserId)))
                                    .flatMap(user -> verificationCodeService.save(
                                                    VerificationCode.builder()
                                                            .otpHash(code)
                                                            .user(user)
                                                            .build()
                                            )
                                    )
                                    .then()
                    )
                    .thenReturn(okMsg)
                    .onErrorReturn(sorryMsg);
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
