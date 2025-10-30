package com.example.database.repo.jpa.telegram;

import com.example.database.entity.telegram.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface TelegramUserRepo extends JpaRepository<TelegramUser, Long> {

    Optional<TelegramUser> findByTelegramUserId(Long telegramUserId);

    @Query("""
            SELECT tu FROM TelegramUser tu
            JOIN FETCH tu.telegramSessions ts
            WHERE tu.telegramUserId = :telegramUserId
            ORDER BY ts.id DESC
            """)
    Optional<TelegramUser> findByTelegramUserIdWithTelegramSessionsDesc(@Param("telegramUserId") Long telegramUserId);
}
