package com.example.telegram.api.controller;

import com.example.telegram.bot.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("${telegram.bot.webhook-path}")
@Log4j2
public class WebhookController {

    @Autowired
    private TelegramBot telegramBot;

    @Value("${telegram.bot.name}")
    private String telegramBotName;

    @PostConstruct
    private void init() {
        log.debug("telegramBot {}", telegramBot);
        log.debug("telegramBotName {}", telegramBotName);
    }

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {

        log.debug("Входящий update с телеграм бота через webHook");
        log.debug(update.getUpdateId());

        return telegramBot.onWebhookUpdateReceived(update);
    }

    @GetMapping(path = {"/info", "/info/"})
    public String getBotInformation() {
        log.debug("Входящий запрос на ендпоинт - /telegram-bot/info");
        return "Hi. I am " + telegramBotName;
    }
}
