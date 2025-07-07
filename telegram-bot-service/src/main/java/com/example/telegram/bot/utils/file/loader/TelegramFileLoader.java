package com.example.telegram.bot.utils.file.loader;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.File;

public interface TelegramFileLoader {

    File load(Message message);

}
