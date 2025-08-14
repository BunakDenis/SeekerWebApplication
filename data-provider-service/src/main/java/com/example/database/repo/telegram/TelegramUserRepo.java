package com.example.database.repo.telegram;

import com.example.database.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserRepo extends JpaRepository<TelegramUser, Long> {

    TelegramUser getTelegramUserById(Long id);

}
