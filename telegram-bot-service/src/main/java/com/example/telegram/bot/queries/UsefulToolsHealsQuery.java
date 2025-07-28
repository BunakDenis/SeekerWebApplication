package com.example.telegram.bot.queries;

import com.example.telegram.api.clients.UsefulToolsClient;
import com.example.telegram.bot.utils.update.UpdateService;
import com.example.telegram.dto.responce.ActuatorHealthResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Data
@RequiredArgsConstructor
@Log4j2
public class UsefulToolsHealsQuery implements Query {

    private final UsefulToolsClient client;
    @Override
    public SendMessage apply(Update update) {
        long chatId = UpdateService.getChatId(update);

        ActuatorHealthResponse response = client.getUsefulToolsHeals();

        log.debug(response);

        SendMessage result = new SendMessage();

        result.setChatId(chatId);

        result.setText("Состояние useful tools service - " + response.getStatus());

        return result;
    }
}
