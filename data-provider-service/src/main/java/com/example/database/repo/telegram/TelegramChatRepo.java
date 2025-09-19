package com.example.database.repo.telegram;

import com.example.database.entity.TelegramChat;
import com.example.database.entity.TelegramUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelegramChatRepo extends JpaRepository<TelegramChat, Long> {

    List<TelegramChat> getAllById(Long id);
    List<TelegramChat> findByTelegramUser(TelegramUser telegramUser);

    @EntityGraph(attributePaths = "telegramUser")
    Optional<TelegramChat> findFirstByIdOrderByIdDesc(Long id);
    @EntityGraph(attributePaths = "telegramUser")
    Optional<TelegramChat> findFirstByTelegramUserIdOrderByIdDesc(Long telegramUserId);

}
