package com.example.database.repo.telegram;

import com.example.database.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VerificationCodeRepo extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findById(Long id);
    @Query("SELECT vc FROM VerificationCode vc " +
            "JOIN vc.user u " +
            "WHERE vc.isActive = true AND " +
            "u.id = :id")
    Optional<VerificationCode> findByUserId(Long id);

    @Query("SELECT vc FROM VerificationCode vc " +
            "JOIN vc.user u " +
            "JOIN u.telegramUsers tu " +
            "WHERE vc.isActive = true AND " +
            "tu.telegramUserId = :telegramUserId")
    Optional<VerificationCode> findActiveByTelegramUserId(Long telegramUserId);
    @Query("SELECT vc FROM VerificationCode vc " +
            "JOIN FETCH vc.user u " +
            "WHERE u.id = :userId")
    List<VerificationCode> findAllByUserId(Long userId);
    @Query("SELECT vc FROM VerificationCode vc " +
            "JOIN FETCH vc.user u " +
            "WHERE vc.isActive = true AND " +
            "u.id = :userId")
    List<VerificationCode> findAllActiveByUserId(Long userId);

}
