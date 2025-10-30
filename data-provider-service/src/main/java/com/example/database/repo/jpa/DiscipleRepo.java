package com.example.database.repo.jpa;

import com.example.database.entity.Disciple;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiscipleRepo extends JpaRepository<Disciple, Long> {

    Boolean existsByCuratorId(Long curatorId);

}
