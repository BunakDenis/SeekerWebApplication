package com.example.database.service;

import com.example.data.models.consts.ExceptionMessageProvider;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.UserDetailsDTO;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.enums.UserRoles;
import com.example.data.models.exception.*;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.database.entity.telegram.TelegramUser;
import com.example.database.entity.User;
import com.example.database.repo.jpa.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;
import static com.example.data.models.utils.EntityUtilsService.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepo repo;
    private final ModelMapperService mapper;


    public ApiResponse<UserDTO> save(User user) {

        User save;

        checkUser(user);

        try {

            save = repo.save(user);

            return success(mapper.toDTO(save, UserDTO.class));

        } catch (Exception e) {
            log.error("Ошибка сохранения юзера User - {}", e.getMessage(), e);
            throw new EntityNotSavedException("Ошибка сохранения юзера " + user);
        }

    }
    public ApiResponse<UserDTO> update(User user) {
        return save(user);
    }
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

        Optional<User> userOptional = repo.findByEmail(email);

        if (userOptional.isPresent())
            return success(mapper.toDTO(userOptional.get(), UserDTO.class));
        
        throw new EntityNotFoundException("Entity User with email " + email + " is not found", new User());
    }
    public ApiResponse<UserDTO> getUserByTelegramUserId(Long id) throws EntityNotFoundException {

        Optional<User> userOptional = repo.findByTelegramUserId(id);

        if (userOptional.isPresent()) return success(mapper.toDTO(userOptional.get(), UserDTO.class));

        throw new EntityNotFoundException("User with telegram user id " + id + " is not found", new User());

    }
    public ApiResponse<UserDTO> getUserByTelegramUserIdWithUserDetails(Long id) throws EntityNotFoundException {

        Optional<User> optionalUser = repo.findByTelegramUsers_IdWithUserDetails(id);

        if (!optionalUser.isPresent()) throw new EntityNotFoundException(
                "User with telegram user id " + id + " is not found",
                new User()
        );

        User user = optionalUser.get();

        ApiResponse<UserDTO> resp = success(mapper.toDTO(user, UserDTO.class));

        UserDetailsDTO userDetailsDTO = mapper.toDTO(user.getUserDetails(), UserDetailsDTO.class);

        resp.addIncludeObject(ResponseIncludeDataKeys.USER_DETAILS.getKeyValue(), userDetailsDTO);

        return resp;

    }
    public ApiResponse<UserDTO> getUserByTelegramUserIdWithTelegramUser(Long id) throws EntityNotFoundException {

        Optional<User> userOptional = repo.findByTelegramUserIdWithTelegramUsers(id);

        if (!userOptional.isPresent()) throw new EntityNotFoundException(
                "User by telegram user id=" + id + ", is not found", new User()
        );

        User user = userOptional.get();

        ApiResponse<UserDTO> resp = success(mapper.toDTO(user, UserDTO.class));

        List<TelegramUser> telegramUsers = user.getTelegramUsers();

        telegramUsers.forEach(tgUser -> log.debug("Telegram user = {}", tgUser));

        TelegramUserDTO telegramUserDTO = new TelegramUserDTO();

        if (!isNull(telegramUsers.get(0)))
            telegramUserDTO = mapper.toDTO(telegramUsers.get(0), TelegramUserDTO.class);

        resp.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(), telegramUserDTO);

        return resp;
    }
    public ApiResponse<UserDTO> getUserByTelegramUserIdFull(Long id) throws EntityNotFoundException {

        Optional<User> optionalUser = repo.findFullByTelegramUser_id(id);

        if (!optionalUser.isPresent())
            throw new EntityNotFoundException("User with telegram user id " + id + " is not found", new User());

        User user = optionalUser.get();

        ApiResponse<UserDTO> resp = success(mapper.toDTO(user, UserDTO.class));

        UserDetailsDTO userDetailsDTO = mapper.toDTO(user.getUserDetails(), UserDetailsDTO.class);

        TelegramUserDTO telegramUserDTO = mapper.toDTO(user.getTelegramUsers().get(0), TelegramUserDTO.class);

        resp.addIncludeObject(ResponseIncludeDataKeys.USER_DETAILS.getKeyValue(), userDetailsDTO);
        resp.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(), telegramUserDTO);

        return resp;
    }
    public ApiResponse<Boolean> existsByUsername(String username) {
        Boolean result = repo.existsByUsername(username);

        return ApiResponse.<Boolean>builder().data(result).build();
    }
    public ApiResponse<Boolean> existsByEmail(String email) {
        Boolean result = repo.existsByEmail(email);

        return ApiResponse.<Boolean>builder().data(result).build();
    }
    public ApiResponse<Boolean> delete(Long id) {
        boolean isExists = repo.existsById(id);

        if (!isExists) return ApiResponseUtilsService.success(
                HttpStatus.OK,
                false,
                ExceptionMessageProvider.getEntityNotFoundExceptionText(
                        "User", "id", Long.toString(id)
                )
        );

        repo.deleteById(id);
        return ApiResponseUtilsService.success(true);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("loadUserByUsername for username {}", username);

        User user = new User();

        ApiResponse<UserDTO> userDto = getUserByUsername(username);

        log.debug(userDto.toString());

        if (Objects.nonNull(userDto.getData())) user = mapper.toEntity(userDto.getData(), User.class);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public UserDetails getDefaultUser() {
        return org.springframework.security.core.userdetails.User.builder()
                .username("user")
                .password("")
                .roles(UserRoles.TOURIST.getRole())
                .build();
    }

    public void checkUser(User user) {

        if (isNull(user)) throw new EntityNullException(
                ExceptionMessageProvider.getEntityNullExceptionText(new User())
        );

        if (isNull(user.getUsername())) throw new EntityNullFieldException("username");

        if (isStringFieldEmpty(user.getUsername())) throw new EntityEmptyFieldException("username");

        if (isNull(user.getPassword())) throw new EntityNullFieldException("password");

        if (isStringFieldEmpty(user.getPassword())) throw new EntityEmptyFieldException("password");

        if (isNull(user.getEmail())) throw new EntityNullFieldException("email");

        if (isStringFieldEmpty(user.getEmail())) throw new EntityEmptyFieldException("email");

    }

}
