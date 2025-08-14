package com.example.database.repo.telegram;

import com.example.database.entity.TelegramChat;
import com.example.database.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelegramChatRepo extends JpaRepository<TelegramChat, Long> {

    List<TelegramChat> getAllById(Long id);

    List<TelegramChat> getChatsByTelegramUserId(TelegramUser telegramUser);

}
