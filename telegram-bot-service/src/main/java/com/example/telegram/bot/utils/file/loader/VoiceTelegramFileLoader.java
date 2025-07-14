package com.example.telegram.bot.utils.file.loader;

import com.example.telegram.bot.TelegramBot;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Voice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

@Component
@Data
@RequiredArgsConstructor
@Log4j2
public class VoiceTelegramFileLoader implements TelegramFileLoader {

    private final ApplicationContext applicationContext;

    private final TelegramBotMessageSender sender;

    @Override
    public File load(Message message) {
        File result = new File("");
        TelegramBot bot = applicationContext.getBean(TelegramBot.class);

        try {
            Voice voice = message.getVoice();
            String mimeType = voice.getMimeType();
            String fileId = voice.getFileId();

            GetFile getFile = new GetFile(fileId);
            org.telegram.telegrambots.meta.api.objects.File fileMeta = bot.execute(getFile);
            String fileUrl = "https://api.telegram.org/file/bot" + bot.getBotToken() + "/" + fileMeta.getFilePath();

            // Сохраняем файл во временную папку
            String fileName = "./src/main/resources/temp/" +
                    voice.getFileUniqueId() + "." +
                    mimeType.substring(mimeType.indexOf("/") + 1);

            File targetFile = new File(fileName);
            targetFile.getParentFile().mkdirs();

            try (InputStream in = new URL(fileUrl).openStream();
                 FileOutputStream out = new FileOutputStream(targetFile)) {
                in.transferTo(out);
            }

            log.debug("Запись с диктофона успешно загружена: " + fileName);

            result = targetFile;

        } catch (Exception e) {
            log.error("Ошибка при загрузке записи с диктофона: " + e.getMessage());
            sender.sendMessage(message.getChatId(), "Ошибка при обработке записи с диктофона.");
        }

        return result;
    }
}
