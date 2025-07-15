package com.example.telegram.bot.multimedia;

import com.example.telegram.api.clients.UsefulToolsClient;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.utils.update.UpdateService;
import com.example.telegram.dto.FileServiceResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;

@Component
@Data
@RequiredArgsConstructor
@Log4j2
public class MultimediaHandler {

    private final TelegramBotMessageSender sender;

    private final UsefulToolsClient usefulToolsClient;

    //private final AudioTelegramFileLoader audioFileLoader;

    //private final VoiceTelegramFileLoader voiceFileLoader;


    public void handleMultimedia(Update update) {

        log.debug("handleMultimedia method");

        File sourceFile = new File("");

        Message message = UpdateService.getMessage(update);
        long chatId = message.getChatId();

        FileServiceResponse newFileName =
                usefulToolsClient.changeFileExtensionBlocking(
                        "test.txt",
                        "exe"
                );

        log.debug("newFileName = " + newFileName);

        //Проверка формата сообщения
        if (message.hasAudio()) {

            log.debug("Юзер загрузил аудио - " + message.getAudio());

            //sourceFile = audioFileLoader.load(message);

        } else if (message.hasVoice()) {

            log.debug("Юзер загрузил запись с диктофона - " + message.getVoice());

            //sourceFile = voiceFileLoader.load(message);
        }
/*
        sender.sendMessage(chatId, "Ожидайте декодирования аудио");

        File convertedAudioFile = new File("");

        //Конвертируем аудио в формат wav для дальнейшего декодирования
        try {
            convertedAudioFile = FfmpegService.convertAudio(sourceFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Error converting audio file " + sourceFile.getName() + e.getMessage());
        }

        File cleanedAudioFile = new File(
                convertedAudioFile.getParent()+ "/" +
                        FileNameChanger.addSuffix(convertedAudioFile.getName(), "_clean")
        );

        //Чистим аудио файл от шумов, декодируем, проверяем орфографию и форматируем текст для отправки
        try {
            FfmpegService.denoiseAudio(convertedAudioFile, cleanedAudioFile);

            String decodingText = decodeMultimedia(cleanedAudioFile);
*/
            /*
            SendAudio audioMessage = new SendAudio();
            audioMessage.setChatId(String.valueOf(chatId));
            audioMessage.setCaption("Обработанное аудио");
            audioMessage.setAudio(new InputFile(cleanedAudioFile));

            sender.sendAudio(audioMessage);
*/
/*
            String checkedText = spellChecker.check(decodingText);

            log.debug(
                    "Проверенный текст: \n" +
                    checkedText
                    );

            String formattedText = formatter.format(checkedText);

            sender.sendMessage(chatId, "Декодированный текст:");

            sender.sendMessage(chatId, formattedText);

        } catch (Exception e) {
            log.error("Ошибка обработки файла - " + e.getMessage(), e);
        }
        */
    }

    private String decodeMultimedia(File audioFile) {
        String result = "";
/*
        if (audioFile.exists()) result = audioDecoder.decode(audioFile.getAbsolutePath());
*/
        return result;
    }

}
