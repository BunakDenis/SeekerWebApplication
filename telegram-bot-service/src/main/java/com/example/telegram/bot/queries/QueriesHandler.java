package com.example.telegram.bot.queries;


import static com.example.telegram.bot.message.MessageProvider.*;

import com.example.telegram.bot.message.TelegramBotMessageSender;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.Optional;

@Component
@Data
@Log4j
public class QueriesHandler {

    private final Map<String, Query> queries;

    @Autowired
    private TelegramBotMessageSender sender;

    public QueriesHandler(@Autowired DecodeAudioQuery decodeAudioQuery) {
        this.queries = Map.of(
                Queries.DECODE_AUDIO.getQuery(), decodeAudioQuery
        );
    }


    public void handleQueries(Update update) {
        log.debug("handleQueries method");
        SendMessage answer;

        Message msg = update.getMessage();
        long chatId = msg.getChatId();

        try {
            Query query = queries.get(msg.getText());
            answer = query.apply(update);
        } catch (NullPointerException e) {
            log.debug("Query - " + msg.getText() + ", не найден в коллекции обрабатываемых Query.");
            answer = new SendMessage(String.valueOf(chatId), UNKNOWN_COMMAND_OR_QUERY);
        }
        sender.sendMessage(answer);
    }
}
