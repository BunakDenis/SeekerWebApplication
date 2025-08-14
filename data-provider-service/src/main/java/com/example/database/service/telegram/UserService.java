package com.example.database.service.telegram;

import com.example.database.entity.User;
import com.example.database.exception.UserNotFoundException;
import com.example.database.repo.telegram.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo repo;


    public User getUserById(Long id) {
        Optional<User> userOptional = repo.findById(id);

        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        throw new UserNotFoundException("User with id " + id + " is not found");
    }

    public User getUserByTelegramUserId(Long id) {
        User user = repo.getUserByTelegramUserId(id);

        if (!Objects.isNull(user)) {
            return user;
        }

        throw new  UserNotFoundException("User with telegram user id " + id + "is not found");

    }

}
