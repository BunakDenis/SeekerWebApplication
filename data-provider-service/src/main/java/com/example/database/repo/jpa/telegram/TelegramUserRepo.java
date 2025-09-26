package com.example.database.repo.jpa.telegram;

import com.example.database.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TelegramUserRepo extends JpaRepository<TelegramUser, Long> {

    Optional<TelegramUser> findByTelegramUserId(Long telegramUserId);
}
