package com.example.database.service.telegram;

import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.database.entity.User;
import com.example.database.exception.UserNotFoundException;
import com.example.database.repo.telegram.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static com.example.database.consts.RequestMessageProvider.SUCCESSES_MSG;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo repo;

    private final ModelMapper mapper;


    public User getUserById(Long id) {
        Optional<User> userOptional = repo.findById(id);

        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        throw new UserNotFoundException("User with id " + id + " is not found");
    }

    public ApiResponse<UserDTO> getUserByTelegramUserId(Long id) throws UserNotFoundException {

        User user = repo.getUserByTelegramUserId(id);

        if (Objects.nonNull(user)) {
            return new ApiResponse<>(HttpStatus.OK, SUCCESSES_MSG, toDTO(user));
        }
        throw new  UserNotFoundException("User with telegram user id " + id + " is not found");
    }

    public User toEntity(UserDTO dto) {
        return mapper.map(dto, User.class);
    }

    public UserDTO toDTO(User user) {
        return mapper.map(user, UserDTO.class);
    }

}
