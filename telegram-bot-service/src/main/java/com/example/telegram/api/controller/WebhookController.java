package com.example.telegram.api.controller;

import com.example.telegram.bot.TelegramBot;
import com.example.telegram.bot.service.AuthService;
import com.example.telegram.bot.service.ModelMapperService;
import com.example.telegram.bot.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

@RestController
@RequestMapping("${telegram.bot.webhook-path}")
@Log4j2
public class WebhookController {

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ModelMapperService mapperService;

    @Value("${telegram.bot.name}")
    private String telegramBotName;

    @PostConstruct
    private void init() {
        log.debug("telegramBotName {}", telegramBotName);
    }

    @PostMapping("/")
    public Mono<ResponseEntity<?>> handleWebhook(
            @RequestHeader(name = "X-Session-Id") String sessionId,
            @RequestBody Update update
    ) {

        log.debug("Метод handleWebhook");

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .doOnNext(auth -> telegramBot.getReactiveHandler().setAuthentication(auth))
                .then(telegramBot.getReactiveHandler().handleUpdate(update))
                .map(sent -> ResponseEntity.ok().body("ok"));
    }

    @GetMapping(path = {"/info", "/info/"})
    public String getBotInformation() {
        log.debug("Входящий запрос на ендпоинт - /telegram-bot/info");
        return "Hi. I am " + telegramBotName;
    }
}
