package com.example.database.service;

import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.UserDetailsDTO;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.database.entity.TelegramUser;
import com.example.database.entity.User;
import com.example.database.exception.UserNotFoundException;
import com.example.database.repo.telegram.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo repo;

    private final ModelMapperService mapper;


    public ApiResponse<UserDTO> getUserById(Long id) {
        Optional<User> userOptional = repo.findById(id);

        if (userOptional.isPresent())
            return success(mapper.toDTO(userOptional.get(), UserDTO.class));

        throw new EntityNotFoundException("User with id " + id + " is not found", new User());
    }

    public ApiResponse<UserDTO> getUserByUsername(String username) {
        Optional<User> userOptional = repo.findByUsername(username);

        if (userOptional.isPresent())
            return success(mapper.toDTO(userOptional.get(), UserDTO.class));

        throw new EntityNotFoundException("User with username " + username + " is not found", new User());
    }

    public ApiResponse<UserDTO> getUserByEmail(String email) {

        Optional<User> userOptional = repo.findByUsername(email);

        if (userOptional.isPresent())
            return success(mapper.toDTO(userOptional.get(), UserDTO.class));

        throw new UserNotFoundException("User with email " + email + " is not found");
    }

    public ApiResponse<UserDTO> getUserByTelegramUserId(Long id) throws UserNotFoundException {

        User user = repo.getUserByTelegramUserId(id);

        if (Objects.nonNull(user))
            return success(mapper.toDTO(user, UserDTO.class));

        throw new  UserNotFoundException("User with telegram user id " + id + " is not found");
    }

    @Transactional
    public ApiResponse<UserDTO> getUserByTelegramUserIdWithUserDetails(Long id) throws UserNotFoundException {

        User user = repo.getUserByTelegramUserId(id);

        if (Objects.nonNull(user)) {

            ApiResponse<UserDTO> resp = success(mapper.toDTO(user, UserDTO.class));

            UserDetailsDTO userDetailsDTO = mapper.toDTO(user.getUserDetails(), UserDetailsDTO.class);

            resp.addIncludeObject(ResponseIncludeDataKeys.USER_DETAILS.getKeyValue(), userDetailsDTO);

            return resp;
        }
        throw new  UserNotFoundException("User with telegram user id " + id + " is not found");
    }

    @Transactional
    public ApiResponse<UserDTO> getUserByTelegramUserIdWithTelegramUser(Long id) throws UserNotFoundException {

        User user = repo.getUserByTelegramUserId(id);

        if (Objects.nonNull(user)) {

            ApiResponse<UserDTO> resp = success(mapper.toDTO(user, UserDTO.class));

            List<TelegramUser> telegramUsers = user.getTelegramUsers();

            Optional<TelegramUser> telegramUserOptional = telegramUsers.stream()
                    .filter(u -> u.getId().equals(id))
                    .findFirst();

            TelegramUserDTO telegramUserDTO = new TelegramUserDTO();

            if (telegramUserOptional.isPresent())
                telegramUserDTO = mapper.toDTO(telegramUserOptional.get(), TelegramUserDTO.class);

            resp.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(), telegramUserDTO);

            return resp;
        }
        throw new  UserNotFoundException("User with telegram user id " + id + " is not found");
    }

    @Transactional
    public ApiResponse<UserDTO> getUserByTelegramUserIdFull(Long id) throws UserNotFoundException {

        User user = repo.getUserByTelegramUserId(id);

        if (Objects.nonNull(user)) {

            ApiResponse<UserDTO> resp = success(mapper.toDTO(user, UserDTO.class));

            UserDetailsDTO userDetailsDTO = mapper.toDTO(user.getUserDetails(), UserDetailsDTO.class);

            List<TelegramUser> telegramUsers = user.getTelegramUsers();
            List<Object> telegramUserDTOList = new ArrayList<>();

            if (!telegramUsers.isEmpty())
                telegramUsers.forEach(tu -> telegramUserDTOList.add(mapper.toDTO(tu, TelegramUserDTO.class)));

            resp.addIncludeObject(ResponseIncludeDataKeys.USER_DETAILS.getKeyValue(), userDetailsDTO);
            resp.addIncludeListObjects(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(), telegramUserDTOList);

            return resp;
        }
        throw new  UserNotFoundException("User with telegram user id " + id + " is not found");
    }

}
