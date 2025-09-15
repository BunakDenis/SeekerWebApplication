package com.example.database.repo.telegram;

import com.example.database.entity.TelegramChat;
import com.example.database.entity.TelegramUser;
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

    Optional<TelegramChat> findLastByTelegramUserIdOrderById(@Param("id") Long id);
    @Query("SELECT c FROM TelegramChat c JOIN TelegramUser tu JOIN FETCH c.telegramUser WHERE tu.id = :id")
    List<TelegramChat> findByTelegramUserIdWithTelegramUser(@Param("id") Long id);

}
