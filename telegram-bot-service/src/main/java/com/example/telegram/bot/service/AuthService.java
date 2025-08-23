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

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;

    /**
     * Помещает Authentication с нашим User в контекст реактивного потока.
     * Возвращает Mono<Void> который можно вписать в chain.filter(...).contextWrite(...)
     */
    /*
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
*/
    /**
     * Удобная проверка - авторизован ли кто-то сейчас.
     */
    /*
    public Mono<Boolean> isAuthenticated() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(auth -> auth != null && auth.isAuthenticated());
    }

     */
}