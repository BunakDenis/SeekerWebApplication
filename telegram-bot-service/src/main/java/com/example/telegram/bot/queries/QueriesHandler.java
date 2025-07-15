package com.example.telegram.bot.queries;


import static com.example.telegram.bot.message.MessageProvider.*;

import com.example.telegram.api.clients.UsefulToolsClient;
import com.example.telegram.bot.message.MessageForWifeProvider;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.utils.update.UpdateService;
import com.example.telegram.dto.ActuatorHealthResponse;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
@Data
@Log4j2
public class QueriesHandler {

    private final Map<String, Query> queries;

    @Autowired
    private TelegramBotMessageSender sender;

    @Autowired
    private UsefulToolsClient usefulToolsClient;

    private static final long WIFE_CHAT_ID = 5098247848L;

    public QueriesHandler(
            @Autowired DecodeAudioQuery decodeAudioQuery,
            @Autowired UsefulToolsHealsQuery usefulToolsQuery
    ) {
        this.queries = Map.of(
                Queries.DECODE_AUDIO.getQuery(), decodeAudioQuery,
                Queries.GET_ACTUAL_USEFUL_TOOLS_HEALS.getQuery(), usefulToolsQuery
        );
    }


    public void handleQueries(Update update) {
        log.debug("handleQueries method");
        SendMessage answer;

        Message msg = UpdateService.getMessage(update);
        long chatId = msg.getChatId();

            try {
                Query query = queries.get(msg.getText());
                answer = query.apply(update);
            } catch (NullPointerException e) {
                log.debug("Query - " + msg.getText() + ", не найден в коллекции обрабатываемых Query.");
                answer = new SendMessage(String.valueOf(chatId), UNKNOWN_COMMAND_OR_QUERY);
            }

        if (update.getMessage().getChatId() == WIFE_CHAT_ID) {
            sender.sendMessage(update.getMessage().getChatId(),
                    MessageForWifeProvider.getMessage());
        } else {
            sender.sendMessage(answer);
        }

    }
}
