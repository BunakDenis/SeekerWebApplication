package com.example.telegram.bot.commands.impl;

import com.example.telegram.bot.commands.Command;
import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

@Component
@Log4j2
@Data
public class RegCommandImpl implements Command {

    @Override
    public Mono<SendMessage> apply(Update update, TelegramChat lastTelegramChat) {
        SendMessage result = new SendMessage();

        result.setChatId(UpdateUtilsService.getChatId(update));
        result.setText("Процедура регистрации находится в разработке");

        return Mono.just(result);
    }
}
