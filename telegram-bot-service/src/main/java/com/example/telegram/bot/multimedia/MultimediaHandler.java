package com.example.telegram.bot.multimedia;

import com.example.telegram.api.clients.UsefulToolsClient;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.utils.file.FileService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;

import java.io.File;

@Component
@Data
@RequiredArgsConstructor
@Log4j2
public class MultimediaHandler {

    private final TelegramBotMessageSender sender;

    private final UsefulToolsClient usefulToolsClient;

    private final FileService fileService;

    //private final AudioTelegramFileLoader audioFileLoader;

    //private final VoiceTelegramFileLoader voiceFileLoader;


    public void handleMultimedia(Update update) {
        log.debug("handleMultimedia method");

        Message message = UpdateUtilsService.getMessage(update);
        long chatId = message.getChatId();

        File audioFile = new File("");

        String sourceFile = "test.txt";

        String newFile = fileService.changeExtension(sourceFile, "exe");

        log.debug("source file {}", sourceFile);
        sender.sendMessage(chatId, "source file " + sourceFile);

        log.debug("new file {}", newFile);
        sender.sendMessage(chatId, "new file " + newFile);

    }

    private String decodeMultimedia(File audioFile) {
        //String result = "";

        usefulToolsClient.decodeAudio()
                .flatMap(response -> {

                    log.debug("Получен FileServiceResponse: " + response);

                    return Mono.empty();

                })
                .doOnSuccess(result -> {
                    log.debug("Успешное выполнение запроса на Useful tools service");
                })
                .onErrorResume(e -> {
                    log.error("Ошибка при обработке multimedia: " + e.getMessage(), e);
                    return Mono.empty();
                });

/*
        if (audioFile.exists()) result = audioDecoder.decode(audioFile.getAbsolutePath());
*/
        return null;
    }

}
