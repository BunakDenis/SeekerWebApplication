package com.example.telegram.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telegram-bot")
@RequiredArgsConstructor
@Log4j2
public class TelegramBotController {

    @Value("telegram.bot.name")
    private String telegramBotName;

    @GetMapping("/")
    public String getDefaultInformation() {
        return "Hi. I am " + telegramBotName;
    }

}
