package com.example.telegram.bot.service;

import com.example.data.models.consts.DefaultEntityValuesConsts;
import com.example.data.models.entity.dto.UserDetailsDTO;
import com.example.data.models.entity.response.CheckUserResponse;
import com.example.data.models.enums.UserRoles;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.telegram.TelegramUserDTO;
import com.example.telegram.api.clients.DataProviderClient;
import com.example.data.models.entity.TelegramUser;
import com.example.data.models.entity.User;
import com.example.data.models.entity.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.data.models.enums.ResponseIncludeDataKeys.TELEGRAM_USER;
import static com.example.data.models.enums.ResponseIncludeDataKeys.USER_DETAILS;

@Service
@Data
@RequiredArgsConstructor
@Builder
@Slf4j
public class UserService implements ReactiveUserDetailsService {


    private final DataProviderClient dataProviderClient;
    private final ModelMapperService mapperService;
    private final ObjectMapper objectMapper;


    public Mono<User> save(User user) {

        if (Objects.isNull(user)) return Mono.empty();

        UserDTO dto = mapperService.toDTO(user, UserDTO.class);

        if (Objects.isNull(dto)) return Mono.empty();

        return dataProviderClient.createUser(dto)
                .flatMap(resp -> Mono.just(mapperService.toEntity(resp.getData(), User.class)));

    }
    public Mono<User> update(User user) {

        if (Objects.isNull(user)) return Mono.empty();

        UserDTO dto = mapperService.toDTO(user, UserDTO.class);

        if (Objects.isNull(dto)) return Mono.empty();

        return dataProviderClient.updateUser(dto)
                .flatMap(resp -> Mono.just(mapperService.toEntity(resp.getData(), User.class)));

    }

    public Mono<ApiResponse<UserDTO>> getUserById(Long id) {
        return dataProviderClient.getUserById(id);
    }

    public Mono<User> getUserByUsername(String username) {
        return dataProviderClient.getUserByUsername(username)
                .flatMap(resp -> Mono.just(mapperService.toEntity(resp.getData(), User.class)));
    }

    public Mono<User> getUserByEmail(String email) {
        return dataProviderClient.getUserByEmail(email)
                .flatMap(resp -> Objects.nonNull(resp.getData()) ?
                        Mono.just(mapperService.toEntity(resp.getData(), User.class)) :
                        Mono.just(User.builder().build())
                );
    }

    public Mono<User> getDefaultUser() {
        return dataProviderClient.getUserByUsername(DefaultEntityValuesConsts.USER_USERNAME_DEFAULT)
                .flatMap(resp -> Mono.just(
                                mapperService.toEntity(resp.getData(), User.class)
                        )
                );
    }

    public Mono<User> getUserByTelegramUserId(Long id) {
        return dataProviderClient.getUserByTelegramUserId(id)
                .flatMap(resp -> Objects.nonNull(resp.getData()) ?
                            Mono.just(mapperService.toEntity(resp.getData(), User.class)) :
                            Mono.empty()
                );
    }
    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserIdWithUserDetails(Long id) {
        return dataProviderClient.getUserByTelegramUserIdWithUserDetails(id);
    }
    public Mono<CheckUserResponse> checkUserInMysticSchoolDB(String email) {
        return dataProviderClient.checkUserAuthInMysticSchoolDbByUserEmail(email)
                .flatMap(resp -> {

                    log.debug("Response: {}", resp);

                    return Mono.just(resp.getData());
                })
                .doOnError(err -> log.error("Ошибка проверки юзера по email=" + email +
                        "\nТекст ошибки - {}", err.getMessage(), err))
                .onErrorResume(err -> Mono.empty());
    }
    public Mono<Boolean> delete(Long id) {
        return dataProviderClient.deleteUserById(id)
                .flatMap(resp -> Mono.just(resp.getData()));
    }
    public User toEntity(ApiResponse<UserDTO> dto) {

        UserDetails userDetails = new UserDetails();
        TelegramUser telegramUser = new TelegramUser();
        List<TelegramUser> telegramUsers = new ArrayList<>();


        User result = mapperService.toEntity(dto.getData(), User.class);

        Object userDetailsDTOObject = dto.getIncludedObject(USER_DETAILS.getKeyValue());

        Object telegramUserDTO = dto.getIncludedObject(TELEGRAM_USER.getKeyValue());

        List<Object> telegramUserDTOList = dto.getIncludedListObjects(TELEGRAM_USER.getKeyValue());


        if (Objects.nonNull(userDetailsDTOObject)) {
            userDetails = mapperService.toEntity(
                    objectMapper.convertValue(userDetailsDTOObject, UserDetailsDTO.class),
                    UserDetails.class
            );

            result.setUserDetails(userDetails);
        }

        if (Objects.nonNull(telegramUserDTO)) {
            telegramUser = mapperService.toEntity(
                    objectMapper.convertValue(telegramUserDTO, TelegramUserDTO.class),
                    TelegramUser.class
            );

            result.setTelegramUsers(List.of(telegramUser));
        }

        if (telegramUserDTOList != null && !telegramUserDTOList.isEmpty()) {
            telegramUserDTOList.forEach(tuDTO -> {
                telegramUsers.add(
                        mapperService.toEntity(
                                objectMapper.convertValue(tuDTO, TelegramUserDTO.class),
                                TelegramUser.class
                        )
                );
            });

            result.setTelegramUsers(telegramUsers);
        }

        return result;
    }
    @Override
    public Mono<org.springframework.security.core.userdetails.UserDetails> findByUsername(String username) {
        return getUserByUsername(username)
                .flatMap(user -> {

                    log.debug("Класс UserService, метод findByUsername, user = {}", user);

                    return Mono.just(
                            org.springframework.security.core.userdetails.User.builder()
                                    .username(user.getUsername())
                                    .password("")
                                    .roles(user.getRole())
                                    .build()
                    );
                });
    }
    public org.springframework.security.core.userdetails.UserDetails getDefaultUserDetails() {
        return org.springframework.security.core.userdetails.User.builder()
                .username("user")
                .password("")
                .roles(UserRoles.TOURIST.getRole())
                .build();
    }

}
