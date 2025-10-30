package com.example.database.repo.jpa;


import com.example.database.entity.Curator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuratorRepo extends JpaRepository<Curator, Long> {

}
