package com.example.telegram.bot.commands.impl;

import com.example.telegram.bot.commands.CommandHandler;
import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.keyboard.ReplyKeyboardMarkupFactory;
import com.example.telegram.bot.message.MessageProvider;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Data
@Slf4j
@RequiredArgsConstructor
public class StartCommandHandlerImpl implements CommandHandler {

    @Override
    public Mono<SendMessage> apply(Update update, TelegramChat lastTelegramChat) {

        log.debug("StartCommandImpl метод apply");

        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        StringBuilder greeting = new StringBuilder();

        String telegramUserFullName = UpdateUtilsService.getTelegramUserFullName(update);

        greeting.append(telegramUserFullName);
        greeting.append("! ");

        greeting.append(MessageProvider.START_MSG);

        SendMessage answer = new SendMessage(String.valueOf(chatId),
                greeting.toString());

        List<KeyboardRow> mainMenuKeyboard = ReplyKeyboardMarkupFactory.getMainMenuKeyboard();
        KeyboardRow favBtnRow = ReplyKeyboardMarkupFactory.getFavBtnRow();
        mainMenuKeyboard.add(0, favBtnRow);

        ReplyKeyboardMarkup replyKeyboard = ReplyKeyboardMarkupFactory.getReplyKeyboard(mainMenuKeyboard);

        answer.setReplyMarkup(replyKeyboard);

        return Mono.just(answer);
    }
}
