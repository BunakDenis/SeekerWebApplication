package com.example.database.repo.telegram;

import com.example.database.entity.TelegramChat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelegramChatRepo extends JpaRepository<TelegramChat, Long> {

    List<TelegramChat> getAllById(Long id);
    @EntityGraph(attributePaths = "telegramUser")
    Optional<TelegramChat> findFirstByTelegramChatIdOrderByIdDesc(Long id);
    @EntityGraph(attributePaths = "telegramUser")
    Optional<TelegramChat> findFirstByTelegramUser_TelegramUserIdOrderByIdDesc(Long telegramUserId);

}
