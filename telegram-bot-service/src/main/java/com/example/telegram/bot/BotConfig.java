package com.example.telegram.bot;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Log4j2
public class BotConfig {

    @Autowired
    private TelegramBot bot;

    @Value("${telegram.bot.webhook-url}")
    private String webhookUrl;

    @Value("${telegram.bot.webhook-path}")
    private String webhookPath;

    @Value("${truth.seeker.office.ip}")
    private String serverIpAddress;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {

            log.debug("Инициализация бота...");
            log.debug("url = " + webhookUrl + webhookPath);
            log.debug("ipAddress = " + serverIpAddress);

            SetWebhook webhook = SetWebhook.builder()
                    .url(webhookUrl + webhookPath)
                    .ipAddress(serverIpAddress)
                    .build();


            telegramBotsApi.registerBot(bot, webhook);

        } catch (TelegramApiException e) {
            log.error("Ошибка инициализации бота {}", e.getMessage(), e);
        }
    }
}
