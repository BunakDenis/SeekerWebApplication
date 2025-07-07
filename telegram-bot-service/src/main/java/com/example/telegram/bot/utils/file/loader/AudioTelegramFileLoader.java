package com.example.telegram.bot.utils.file.loader;

import com.example.telegram.bot.TelegramBot;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

@Component
@Log4j
@RequiredArgsConstructor
public class AudioTelegramFileLoader implements TelegramFileLoader {

    private final ApplicationContext applicationContext;

    private final TelegramBotMessageSender sender;

    @Override
    public File load(Message message) {
        File result = new File("");
        TelegramBot bot = applicationContext.getBean(TelegramBot.class);

        try {
            Audio audio = message.getAudio();
            String fileId = audio.getFileId();

            GetFile getFile = new GetFile(fileId);
            org.telegram.telegrambots.meta.api.objects.File fileMeta = bot.execute(getFile);
            String fileUrl = "https://api.telegram.org/file/bot" + bot.getBotToken() + "/" + fileMeta.getFilePath();

            // Сохраняем файл во временную папку
            String fileName = "./src/main/resources/temp/" + audio.getFileName();
            File targetFile = new File(fileName);
            targetFile.getParentFile().mkdirs();

            try (InputStream in = new URL(fileUrl).openStream();
                 FileOutputStream out = new FileOutputStream(targetFile)) {
                in.transferTo(out);
            }

            log.debug("Аудио успешно загружено: " + fileName);

            result = targetFile;

        } catch (Exception e) {
            log.error("Ошибка при загрузке аудио: " + e.getMessage());
            sender.sendMessage(message.getChatId(), "Ошибка при обработке аудио.");
        }

        return result;
    }
}
