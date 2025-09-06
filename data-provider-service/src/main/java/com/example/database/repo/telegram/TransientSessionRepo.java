package com.example.database.repo.telegram;

import com.example.database.entity.TransientSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransientSessionRepo extends JpaRepository<TransientSession, Long> {

    Optional<TransientSession> findById(Long id);

}
