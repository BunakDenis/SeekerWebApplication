package com.example.database.repo.telegram;

import com.example.database.entity.TelegramSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TelegramSessionRepo extends JpaRepository<TelegramSession, Long> {

    @Query("SELECT s FROM TelegramSession s " +
            "JOIN s.telegramUser tu " +
            "JOIN tu.user u " +
            "WHERE tu.id = :telegramUserId")
    TelegramSession getTelegramSessionByTelegramUserId(@Param("telegramUserId") Long telegramUserId);

}
