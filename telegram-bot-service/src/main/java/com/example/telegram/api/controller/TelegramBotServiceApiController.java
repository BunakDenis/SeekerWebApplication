package com.example.telegram.api.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${telegram.bot.api.path}")
@Slf4j
public class TelegramBotServiceApiController {

    @Value("${telegram.bot.name}")
    private String telegramBotName;

    @GetMapping(path = {"/info", "/info/"})
    public String getBotInformation() {
        log.debug("Входящий запрос на ендпоинт - /telegram-bot/info");
        return "Hi. I am " + telegramBotName;
    }

}
