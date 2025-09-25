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
import reactor.core.publisher.Mono;

import java.util.Objects;

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
     * Авторизация юзера в телеграм боте через команду "/authorize"
     */
    public Mono<Boolean> isRegistered(String email, Update update) {
        return userService.getUserByEmail(email)
                .flatMap(user -> {

                    if (Objects.nonNull(user)) {
                        return Mono.just(true);
                    } else {
                        return userService.checkUserInMysticSchoolDB(email)
                                .flatMap(resp -> Mono.just(resp.isActive()));
                    }

                });
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