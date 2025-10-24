package com.example.telegram.bot.commands.impl;

import com.example.telegram.bot.chat.UiElements;
import com.example.telegram.bot.commands.CommandHandler;
import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.service.TelegramChatService;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Log4j2
@Data
@RequiredArgsConstructor
public class RegCommandHandlerImpl implements CommandHandler {

    private final TelegramChatService chatService;

    @Override
    public Mono<SendMessage> apply(Update update, TelegramChat lastTelegramChat) {
        SendMessage result = new SendMessage();

        result.setChatId(UpdateUtilsService.getChatId(update));
        result.setText(MessageProvider.REGISTERED_MSG);

        result.setParseMode(ParseMode.HTML);
        result.setDisableWebPagePreview(false);

        TelegramChat chatForSave = TelegramChat.builder()
                .telegramChatId(UpdateUtilsService.getChatId(update))
                .uiElement(UiElements.COMMAND.getUiElement())
                .uiElementValue(Commands.REGISTER.getCommand())
                .chatState("")
                .telegramUser(lastTelegramChat.getTelegramUser())
                .build();

        return chatService.save(chatForSave)
                .then(Mono.just(result));
    }
}
