package com.example.telegram.bot.queries.impl;

import com.example.telegram.api.clients.UsefulToolsClient;
import com.example.telegram.bot.queries.QueryHandler;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import com.example.telegram.dto.responce.ActuatorHealthResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

@Component
@Data
@RequiredArgsConstructor
@Log4j2
public class UsefulToolsHealsQueryHandlerImpl implements QueryHandler {

    private final UsefulToolsClient client;
    @Override
    public SendMessage apply(Update update) {
        long chatId = UpdateUtilsService.getChatId(update);

        ActuatorHealthResponse response = new ActuatorHealthResponse();

        client.getUsefulToolsHeals()
                .flatMap(resp -> {
                    // Асинхронная обработка response
                    log.debug("Результат изменения расширения: " + resp.getStatus());
                    return Mono.empty();
                })
                .onErrorResume(e -> {
                    // Обработка ошибок
                    log.error("Ошибка при изменении расширения файла: " + e.getMessage());
                    return Mono.empty(); // Или вернуть другой Mono, чтобы продолжить выполнение
                });

        SendMessage result = new SendMessage();

        result.setChatId(chatId);

        result.setText("Состояние useful tools service - " + response.getStatus());

        return result;
    }
}
