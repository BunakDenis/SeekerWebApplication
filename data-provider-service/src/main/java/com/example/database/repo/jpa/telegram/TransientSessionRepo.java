package com.example.database.repo.jpa.telegram;

import com.example.database.entity.PersistentSession;
import com.example.database.entity.TransientSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransientSessionRepo extends JpaRepository<TransientSession, Long> {

    Optional<TransientSession> findById(Long id);

    @Query("""
            SELECT tr FROM TransientSession tr
            WHERE tr.isActive = true AND
            tr.id = :id
            """)
    Optional<TransientSession> findActiveById(@Param("id") Long id);

    @Query("""
            SELECT tr FROM TransientSession tr
            JOIN tr.telegramSession ts
            JOIN ts.telegramUser tu
            WHERE tu.telegramUserId = :telegramUserId
            AND tr.isActive = true
            """)
    Optional<TransientSession> findActiveByTelegramUserId(@Param("telegramUserId") Long id);

    List<TransientSession> findAllByTelegramSessionId(Long telegramSession);

}
