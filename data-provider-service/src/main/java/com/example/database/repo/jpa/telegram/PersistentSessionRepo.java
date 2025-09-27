package com.example.database.repo.jpa.telegram;

import com.example.database.entity.PersistentSession;
import com.example.database.entity.TransientSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersistentSessionRepo extends JpaRepository<PersistentSession, Long> {

    Optional<PersistentSession> findById(Long id);

    @Query("""
            SELECT pr FROM PersistentSession pr
            WHERE pr.isActive = true AND
            pr.id = :id
            """)
    Optional<PersistentSession> findActiveById(@Param("id") Long id);

    @Query("""
            SELECT pr FROM PersistentSession pr
            JOIN pr.telegramSession ts
            JOIN ts.telegramUser tu
            WHERE tu.telegramUserId = :telegramUserId
            AND pr.isActive = true
            """)
    Optional<PersistentSession> findActiveByTelegramUserId(@Param("telegramUserId") Long id);

    List<PersistentSession> findAllByTelegramSessionId(Long sessionId);

}
