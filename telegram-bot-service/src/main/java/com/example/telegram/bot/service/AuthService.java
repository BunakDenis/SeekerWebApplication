package com.example.telegram.bot.service;

import com.example.telegram.api.clients.DataProviderClient;
import com.example.telegram.bot.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;

    /**
     * Помещает Authentication с нашим User в контекст реактивного потока.
     * Возвращает Mono<Void> который можно вписать в chain.filter(...).contextWrite(...)
     */
    public Mono<Void> authenticate(UserDetails user) {

        log.debug("Начало метода authenticate user - {}", user);

        if (user == null) {
            return Mono.empty();
        }

        // Указываем principal как сам доменный User (или DTO), чтобы @AuthenticationPrincipal возвращал его
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user,                  // <-- principal = UserDetails
                true,
                user.getAuthorities()
        );

        SecurityContext context = new SecurityContextImpl(auth);

        log.debug("Конец метода authenticate. Context = {}", context);

        // Возврат Mono<Void> не обязателен, но удобно
        return Mono.<Void>empty()
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
    }

    /**
     * Возвращает текущего пользователя (principal) из Reactive SecurityContext.
     * Попытается привести principal к типу org.springframework.security.core.userdetails.UserDetails.
     */
    public Mono<org.springframework.security.core.userdetails.UserDetails> getCurrentUser() {

        log.debug("Начало метода getCurrentUser");

        Mono<UserDetails> result = ReactiveSecurityContextHolder.getContext()
                .map(context -> {
                    log.debug("Security Context = {}", context);
                    return context.getAuthentication();
                })
                .filter(Objects::nonNull)
                .map(auth -> {
                    log.debug("Метод getCurrentUser {}", auth);
                    return auth.getPrincipal();
                })
                .flatMap(principal -> {
                    log.debug("Метод getCurrentUser {}", principal);
                    // Если principal — строка (email) или другой тип, можно попытаться получить через детали:
                    if (principal instanceof UserDetails) {
                        return Mono.just((UserDetails) principal);
                    }
                    return Mono.just(userService.createCurrentUser());
                });

        log.debug("Конец метода getCurrentUser");

        return result;
    }

    /**
     * Удобная проверка - авторизован ли кто-то сейчас.
     */
    public Mono<Boolean> isAuthenticated() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(auth -> auth != null && auth.isAuthenticated());
    }
}