package com.example.database.repo.telegram;

import com.example.database.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VerificationCodeRepo extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findById(Long id);
    Optional<VerificationCode> findByUserId(Long id);

    @Query("SELECT vc FROM VerificationCode vc " +
            "JOIN vc.user u " +
            "JOIN u.telegramUsers tu " +
            "WHERE tu.id = :telegramUserId")
    Optional<VerificationCode> findByTelegramUserId(Long telegramUserId);

}
