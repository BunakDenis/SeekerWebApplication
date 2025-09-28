package com.example.telegram.bot.service;

import com.example.data.models.consts.WarnMessageProvider;
import com.example.data.models.entity.TelegramUser;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
*/

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {


    private final UserService userService;
    private final TelegramUserService telegramUserService;
    private final TelegramSessionService telegramSessionService;
    private final TelegramBotMessageSender sender;
    private final ModelMapperService mapperService;


    /**
     * Авторизация юзера через TelegramUserAuthFilter
     * Помещает Authentication с нашим User в контекст реактивного потока.
     * Возвращает Mono<Authentication> который можно вписать в chain.filter(...).contextWrite(...)
     */
    public Mono<Void> authenticate(Update update) {

        log.debug("Начало метода authenticate");

        Long telegramUserId = UpdateUtilsService.getTelegramUserId(update);
        Long chatId = UpdateUtilsService.getChatId(update);

        return userService.getUserByTelegramUserId(telegramUserId)
                .flatMap(user -> {
                    log.debug("User {}", user);
                    return userService.findByUsername(user.getUsername());
                })
                .switchIfEmpty(
                        userService.getDefaultUser()
                                .flatMap(user -> {

                                    TelegramUser tgUser = TelegramUser.builder()
                                            .user(user)
                                            .username(UpdateUtilsService.getTelegramUsername(update))
                                            .isActive(true)
                                            .build();

                                    return telegramUserService.save(tgUser);
                                })
                                .flatMap(savedTgUser -> Mono.just(userService.getDefaultUserDetails()))
                )
                .flatMap(userDetails ->
                        Mono.zip(
                                telegramSessionService.checkSessionsExpired(telegramUserId, userDetails),
                                Mono.just(userDetails))
                )
                .doOnError(err -> log.error("Ошибка проверки telegram session - {}", err.getMessage()))
                .onErrorResume(err -> {

                    sender.sendMessage(chatId, WarnMessageProvider.RE_AUTHORIZATION_MSG);

                    return Mono.zip(
                            Mono.just(false),
                            Mono.just(userService.getDefaultUserDetails())
                    );
                })
                .flatMap(tuple -> {

                    UserDetails userDetails = tuple.getT2();

                    Authentication auth = getAuthenticationByUserDetails(userDetails);

                    log.debug("Authentication = {}", auth);

                    return Mono.just(auth);
                })
                .flatMap(authentication -> {
                    SecurityContext newContext = new SecurityContextImpl(authentication);

                    return Mono.just(
                            ReactiveSecurityContextHolder.withSecurityContext(Mono.just(newContext))
                    );
                })
                .then()
                .doOnSuccess(v -> log.debug("Контекст безопасности успешно установлен"))
                .doFinally(signalType -> log.debug("Конец метода authenticate: {}", signalType));
    }

    /**
     * Если User с telegram_user_id есть в БД
     * Метод вернёт Mono<TelegramUser> с соответствующим User
     *
     * Если в БД его нет, тогда метод вернёт Mono<TelegramUser> с информацией заполненной в профиле Telegram
     * зарегистрированного под дефолтным User
     */
    public Mono<TelegramUser> registeredAsDefaultUserIfNotExists(Update update) {

        User telegramApiUser = UpdateUtilsService.getTelegramUser(update);
        Long telegramUserId = UpdateUtilsService.getTelegramUserId(update);

        return userService.getUserByTelegramUserIdWithTelegramUser(telegramUserId)
                .switchIfEmpty(
                        userService.getDefaultUser()
                                .flatMap(defaultUser -> {

                                    TelegramUser telegramUser =
                                            mapperService.apiTelegramUserToEntity(telegramApiUser);

                                    telegramUser.setUser(defaultUser);

                                    return telegramUserService.save(telegramUser)
                                            .flatMap(tgUser -> Mono.just(tgUser.getUser()));
                                })
                )
                .onErrorReturn(new com.example.data.models.entity.User())
                .flatMap(user -> Mono.just(user.getTelegramUsers().get(0)));
    }

    /**
     * Проверки наличия записи User и TelegramUser в БД
     * Проверка User в БД по email
     */
    public Mono<Boolean> isRegistered(String email, Update update) {

        Long telegramUserId = UpdateUtilsService.getTelegramUserId(update);

        return userService.getUserByEmail(email)
                .flatMap(user -> Mono.just(true))
                .switchIfEmpty(
                        userService.getUserByTelegramUserId(telegramUserId)
                                .flatMap(user -> Mono.just(true))
                                .switchIfEmpty(
                                        userService.checkUserInMysticSchoolDB(email)
                                                .flatMap(checkUserResponse -> {
                                                    if (checkUserResponse.isFound() && checkUserResponse.isActive()) {
                                                        return Mono.just(true);
                                                    }
                                                    return Mono.just(false);
                                                })
                                )

                )
                .doOnError(err ->
                        log.error(
                                "Ошибка проверки регистрации User по email={} и telegramUserId={}",
                                email, telegramUserId)
                )
                .onErrorResume(err -> Mono.just(false));
    }

    /**
     * Удобная проверка - авторизован ли кто-то сейчас.
     */
    public Mono<Boolean> isAuthenticated() {

        log.debug("Начало метода isAuthenticated");

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(auth -> auth != null && auth.isAuthenticated());
    }

    private Authentication getAuthenticationByUserDetails(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}