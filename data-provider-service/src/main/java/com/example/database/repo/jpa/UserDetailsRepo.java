package com.example.database.repo.jpa;

import com.example.database.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepo extends JpaRepository<UserDetails, Long> {

    UserDetails getByUserId(Long userId);

}
