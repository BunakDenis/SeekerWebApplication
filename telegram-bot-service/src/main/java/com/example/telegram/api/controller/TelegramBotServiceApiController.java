package com.example.telegram.api.controller;


import com.example.data.models.entity.response.ApiResponse;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${telegram.bot.api.path}")
@RequiredArgsConstructor
@Slf4j
public class TelegramBotServiceApiController {

    @Value("${telegram.bot.name}")
    private String telegramBotName;
    private final TelegramBotMessageSender sender;

    @GetMapping(path = {"/info", "/info/"})
    public String getBotInformation() {
        log.debug("Входящий запрос на ендпоинт - /telegram-bot/info");
        return "Hi. I am " + telegramBotName;
    }

    @PostMapping(path = {"/send_message", "/send_message/"})
    public ResponseEntity<ApiResponse<Boolean>> sendMessage(
            @RequestParam("chat_id") Long chatId,
            @RequestParam("message") String message
    ) {
        sender.sendMessage(chatId, message);

        return ResponseEntity.ok().body(
                ApiResponse.<Boolean>builder().data(true).build()
        );
    }
}
