package com.example.telegram.bot.queries;


import com.example.data.models.entity.TelegramChat;
import com.example.telegram.api.clients.UsefulToolsClient;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.queries.impl.DecodeAudioQueryHandlerImpl;
import com.example.telegram.bot.queries.impl.UsefulToolsHealsQueryHandlerImpl;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import com.example.telegram.bot.message.MessageForWifeProvider;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Component
@Data
@Log4j2
public class QueriesHandler {


    @Autowired
    private TelegramBotMessageSender sender;
    @Autowired
    private QueriesHandlersService queryService;
    private static final long WIFE_CHAT_ID = 5098247848L;


    public Mono<Boolean> handleQueries(Update update, TelegramChat lastChat) {

        log.debug("handleQueries method");
        SendMessage answer;

        String query = UpdateUtilsService.getMessageText(update);
        String lastQuery = Objects.requireNonNullElse(lastChat.getUiElementValue(), "");
        long chatId = UpdateUtilsService.getChatId(update);

        if (!lastQuery.isEmpty()) query = lastQuery;

        QueryHandler queryHandler = queryService.getQueryHandler(query);

        if (Objects.nonNull(queryHandler)) {
            answer = queryHandler.apply(update);
        } else {
            log.debug("Для query - {}, не найден handler." + query);
            answer = new SendMessage(String.valueOf(chatId), MessageProvider.UNKNOWN_COMMAND_OR_QUERY);
        }

        if (chatId == WIFE_CHAT_ID) {
            sender.sendMessage(update.getMessage().getChatId(),
                    MessageForWifeProvider.getMessage());
        } else {
            sender.sendMessage(answer);
        }

        return Mono.just(true);
    }
}
