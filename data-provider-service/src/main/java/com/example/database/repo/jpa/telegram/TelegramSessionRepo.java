package com.example.database.repo.jpa.telegram;

import com.example.database.entity.telegram.TelegramSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface TelegramSessionRepo extends JpaRepository<TelegramSession, Long> {

    @Query("""
           SELECT ts
           FROM TelegramSession ts
           JOIN FETCH ts.telegramUser tu
           WHERE ts.id = :id
           """)
    Optional<TelegramSession> findWithTelegramUserById(@Param("id") Long id);

    /*
        TODO
            1. Добавить сортировку по убыванию
            2. Добавить List как результат
            3. В сервисе сделать перебор сессий
     */
    @Query("""
            SELECT s FROM TelegramSession s
            JOIN FETCH s.telegramUser tu
            WHERE tu.telegramUserId = :telegramUserId
            """)
    Optional<TelegramSession> getTelegramSessionByTelegramUserId(@Param("telegramUserId") Long telegramUserId);

}
