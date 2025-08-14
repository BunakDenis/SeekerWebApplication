package com.example.database.repo.telegram;

import com.example.database.entity.TelegramUser;
import com.example.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN TelegramUser tu ON u.id = tu.user.id WHERE tu.id = :telegramUserId")
    User getUserByTelegramUserId(@Param("telegramUserId") Long telegramUserId);

}
