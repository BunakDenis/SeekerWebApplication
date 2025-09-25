package com.example.telegram.bot.commands.impl;


import com.example.data.models.consts.WarnMessageProvider;
import com.example.data.models.entity.*;
import com.example.data.models.exception.ExpiredVerificationCodeException;
import com.example.data.models.exception.NotValidVerificationCodeException;
import com.example.telegram.bot.chat.UiElements;
import com.example.telegram.bot.chat.states.DialogStates;
import com.example.telegram.bot.commands.CommandHandler;
import com.example.telegram.bot.keyboard.ReplyKeyboardMarkupProvider;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.service.*;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import com.example.utils.generator.GenerationService;
import com.example.utils.sender.EmailService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;


import java.util.List;

import static com.example.data.models.consts.WarnMessageProvider.*;
import static com.example.data.models.utils.EntityUtilsService.*;


@Component
@Data
@Slf4j
@RequiredArgsConstructor
public class AuthCommandHandlerImpl implements CommandHandler {


    @Value("${telegram.bot.name}")
    private String botName;
    private final AuthService authService;
    private final UserService userService;
    private final TelegramUserService telegramUserService;
    private final TelegramChatService chatService;
    private final TelegramSessionService telegramSessionService;
    private final TransientSessionService transientSessionService;
    private final PersistentSessionService persistentSessionService;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;


    @Override
    public Mono<SendMessage> apply(Update update, TelegramChat chat) {

        TelegramChat newChat = new TelegramChat();

        newChat.setTelegramChatId(chat.getTelegramChatId());
        newChat.setUiElement(UiElements.COMMAND.getUiElement());
        newChat.setUiElementValue(Commands.AUTHORIZE.getCommand());
        newChat.setChatState("");
        newChat.setTelegramUser(chat.getTelegramUser());

        return Mono.just("")
                .flatMap(resp -> {
                    log.info("AuthCommandImpl метод apply");

                    log.debug("Последний чат {}", chat);

                    String msgText = UpdateUtilsService.getMessageText(update);

                    String chatState = chat.getChatState();

                    if (chatState.isEmpty() || isNull(chatState)) {

                        return emailInputtingStateHandler(update, newChat);

                    } else if (msgText.equals(DialogStates.ENTER_EMAIL.getDialogState()) ||
                            chatState.equals(DialogStates.ENTER_EMAIL.getDialogState())) {

                        return emailCheckingStateHandler(update, newChat);

                    } else if (msgText.equals(DialogStates.EMAIL_VERIFICATION.getDialogState()) ||
                            chatState.equals(DialogStates.EMAIL_VERIFICATION.getDialogState())) {

                        return verificationCodeValidatingStateHandler(update, newChat);

                    }

                    return Mono.zip(
                            Mono.just(
                                    new SendMessage(
                                            UpdateUtilsService.getStringChatId(update),
                                            MessageProvider.UNKNOWN_COMMAND_OR_QUERY
                                    )
                            ),
                            Mono.just(newChat)
                    );
                })
                .flatMap(tuple -> chatService.save(tuple.getT2())
                        .flatMap(resp -> {
                            log.debug("Сохранённый чат {}", resp);
                            log.debug("Telegram user={}", resp.getTelegramUser());
                            return Mono.just(tuple.getT1());
                        })
                );
    }

    private Mono<Tuple2<SendMessage, TelegramChat>> emailInputtingStateHandler(Update update, TelegramChat chat) {
        return Mono.just("")
                .flatMap(some -> {

                    log.info("Стадия ввода юзером емейла");

                    chat.setChatState(DialogStates.ENTER_EMAIL.getDialogState());

                    return Mono.zip(
                            Mono.just(new SendMessage(
                                    UpdateUtilsService.getStringChatId(update),
                                    MessageProvider.EMAIL_CHECKING_MSG
                            )),
                            Mono.just(chat)
                    );
                });

    }

    private Mono<Tuple2<SendMessage, TelegramChat>> emailCheckingStateHandler(Update update, TelegramChat chat) {

        final long chatId = UpdateUtilsService.getChatId(update);
        final Long tgUserId = UpdateUtilsService.getTelegramUserId(update);
        final String email = UpdateUtilsService.getMessageText(update);

        return Mono.defer(() -> {
            // 1) Валидация email — синхронно -> map / just
            if (!emailService.isEmailAddressValid(email)) {
                return Mono.zip(
                        Mono.just(new SendMessage(String.valueOf(chatId), getNotValidEmailAddress(email))),
                        Mono.just(chat)
                );
            }

            log.debug("Метод emailCheckingStateHandler");

            return authService.isRegistered(email, update)
                    .flatMap(isRegistered -> {

                        if (isRegistered) {

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
                                                    .switchIfEmpty(
                                                            userService.getUserByEmail(email)
                                                                    .flatMap(user -> {
                                                                        TelegramUser newTgUser = TelegramUser.builder()
                                                                                .username(UpdateUtilsService.getTelegramUserFullName(update))
                                                                                .isActive(true)
                                                                                .user(user)
                                                                                .build();

                                                                        return telegramUserService.save(newTgUser)
                                                                                .flatMap(tgUser -> Mono.just(user));

                                                                    })
                                                    )
                                                    .flatMap(user -> verificationCodeService.save(
                                                                    VerificationCode.builder()
                                                                            .otpHash(code)
                                                                            .user(user)
                                                                            .build()
                                                            )
                                                    )
                                                    .flatMap(verificationCode -> {
                                                        chat.setChatState(DialogStates.EMAIL_VERIFICATION.getDialogState());
                                                        return Mono.just(chat);
                                                    })
                                    )
                                    .flatMap(savedChat -> Mono.zip(Mono.just(okMsg), Mono.just(savedChat)))
                                    .onErrorResume(err -> {

                                        log.error("Ошибка в методе emailCheckingStateHandler {}", err.getMessage());
                                        return Mono.zip(Mono.just(sorryMsg), Mono.just(chat));
                                    });

                        } else {
                            return Mono.zip(Mono.just(
                                            new SendMessage(
                                                    Long.toString(chatId),
                                                    getNotValidInputEmailAddress(email)
                                            )
                                    ), Mono.just(chat)
                            );
                        }

                    });
        });
    }

    private Mono<Tuple2<SendMessage, TelegramChat>> verificationCodeValidatingStateHandler(Update update, TelegramChat chat) {

        log.info("Стадия проверки введённого юзером кода верификации");

        Long telegramUserId = UpdateUtilsService.getTelegramUserId(update);
        String verificationCode = UpdateUtilsService.getMessageText(update);

        String currentUserFullName = UpdateUtilsService.getTelegramUserFullName(update);

        return verificationCodeService.checkCode(telegramUserId, verificationCode)
                .flatMap(isCodeValid -> {

                    SendMessage result = new SendMessage();
                    result.setChatId(UpdateUtilsService.getStringChatId(update));

                    log.debug("Is code valid {}", isCodeValid);

                    chat.setUiElement("");
                    chat.setUiElementValue("");
                    chat.setChatState("");

                    result.setText(MessageProvider.getSuccessesAuthorizationMsg(currentUserFullName));

                    return telegramUserService.getById(telegramUserId)
                            .flatMap(tgUser -> {
                                TelegramSession session = TelegramSession.builder()
                                        .isActive(true)
                                        .telegramUser(tgUser)
                                        .build();

                                return telegramSessionService.save(session);
                            })
                            .flatMap(session -> {

                                TransientSession transientSession = TransientSession.builder()
                                        .telegramSession(session)
                                        .isActive(true)
                                        .build();

                                return Mono.zip(Mono.just(session), transientSessionService.save(transientSession));
                            })
                            .flatMap(tuple -> {

                                TelegramSession session = tuple.getT1();
                                TransientSession transientSession = tuple.getT2();

                                PersistentSession persistentSession = PersistentSession.builder()
                                        .telegramSession(session)
                                        .isActive(true)
                                        .build();

                                return persistentSessionService.save(persistentSession)
                                        .flatMap(savedPersistentSession -> {
                                            session.setTransientSessions(List.of(transientSession));
                                            session.setPersistentSessions(List.of(savedPersistentSession));
                                            return telegramSessionService.save(session);
                                        });
                            })
                            .then(
                                    Mono.zip(
                                            Mono.just(result),
                                            Mono.just(chat)
                                    )
                            );
                })
                .doOnError(error -> log.error("Ошибка проверки верификационного кода - {}", error))
                .onErrorResume(error -> {

                    ReplyKeyboardMarkup notValidEmailKeyboard = ReplyKeyboardMarkupProvider.getNotValidEmailKeyboard();
                    SendMessage result = new SendMessage();
                    result.setChatId(UpdateUtilsService.getStringChatId(update));

                    log.error("Класс ошибки - {}", error.getClass());
                    log.error("Ошибка проверки верификационного кода {}", error.getMessage(), error);

                    log.debug(
                            "error instanceof NotValidVerificationCodeException = {}",
                            error instanceof NotValidVerificationCodeException
                    );

                    log.debug(
                            "error instanceof ExpiredVerificationCodeException = {}",
                            error instanceof ExpiredVerificationCodeException
                    );

                    if (error instanceof NotValidVerificationCodeException) {
                        result.setText(NOT_VALID_VERIFICATION_CODE);
                        result.setReplyMarkup(notValidEmailKeyboard);
                    } else if (error instanceof ExpiredVerificationCodeException) {
                        result.setText(EXPIRED_VERIFICATION_CODE);
                        result.setReplyMarkup(notValidEmailKeyboard);
                        chat.setChatState(DialogStates.ENTER_EMAIL.getDialogState());
                    } else {
                        result.setText(WarnMessageProvider.getSorryMsg("бот временно недоступен, попробуйте обратится к боту позже"));
                    }

                    return Mono.zip(
                            Mono.just(result),
                            Mono.just(chat)
                    );

                });
    }

}
