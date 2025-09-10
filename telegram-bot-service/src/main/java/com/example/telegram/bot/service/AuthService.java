package com.example.telegram.bot.service;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {


    private final UserService userService;
    private final TelegramSessionService telegramSessionService;


    /**
     * Помещает Authentication с нашим User в контекст реактивного потока.
     * Возвращает Mono<Authentication> который можно вписать в chain.filter(...).contextWrite(...)
     */
    public Mono<Authentication> authenticate(Long telegramUserId) {

        log.debug("Начало метода authenticate");

        return userService.getUserByTelegramUserId(telegramUserId)
                .flatMap(user -> {
                    log.debug("User {}", user);
                    return userService.findByUsername(user.getUsername());
                })
                .flatMap(userDetails ->
                        Mono.zip(
                                telegramSessionService.checkSessionsExpired(telegramUserId, userDetails),
                                Mono.just(userDetails))
                )
                .flatMap(tuple -> {

                    Boolean isSessionsExpired = tuple.getT1();
                    UserDetails userDetails = tuple.getT2();

                    if (!isSessionsExpired) return Mono.just(userDetails);

                    return Mono.just(userService.getDefaultUser());

                })
                .flatMap(userDetails -> {

                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    log.debug("Authentication = {}", auth);
                    return Mono.just(auth);
                })
                .filter(Objects::nonNull)
                .flatMap(auth -> {

                    SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

                    emptyContext.setAuthentication(auth);

                    log.debug("TelegramUserAuthFilter end.");

                    return Mono.just(auth);

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
}