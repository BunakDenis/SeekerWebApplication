package com.example.database.repo.jpa.telegram;

import com.example.database.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {


    @Query("""
       SELECT u
       FROM User u
       JOIN u.telegramUsers tu
       WHERE tu.telegramUserId = :telegramUserId
       """)
    Optional<User> findByTelegramUserId(@Param("telegramUserId") Long telegramUserId);
    @Query("""
              SELECT u FROM User u 
              JOIN FETCH u.telegramUsers tu 
              WHERE tu.telegramUserId = :telegramUserId
    """)
    Optional<User> findByTelegramUserIdWithTelegramUsers(@Param("telegramUserId") Long telegramUserId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @Query("""
              SELECT u FROM User u 
              JOIN u.telegramUsers tu 
              JOIN FETCH u.userDetails d 
              WHERE tu.telegramUserId = :telegramUserId
    """)
    Optional<User> findByTelegramUsers_IdWithUserDetails(@Param("telegramUserId") Long id);
    @Query("""
              SELECT u FROM User u 
              JOIN FETCH u.telegramUsers tu 
              JOIN FETCH u.userDetails d 
              WHERE tu.telegramUserId = :telegramUserId
    """)
    Optional<User> findFullByTelegramUser_id(@Param("telegramUserId") Long id);
}
