package com.example.database.repo.telegram;

import com.example.data.models.entity.dto.telegram.PersistentSessionDTO;
import com.example.database.entity.PersistentSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersistentSessionRepo extends JpaRepository<PersistentSession, Long> {

    Optional<PersistentSession> findById(Long id);

}
