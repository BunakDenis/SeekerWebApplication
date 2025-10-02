package com.example.database.api.controller;


import com.example.data.models.consts.ExceptionMessageProvider;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.response.CheckUserResponse;
import com.example.data.models.exception.EntityNullException;
import com.example.database.api.client.MysticSchoolClient;
import com.example.database.entity.User;
import com.example.database.service.ModelMapperService;
import com.example.database.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.example.data.models.utils.ApiResponseUtilsService.success;
import static com.example.data.models.utils.EntityUtilsService.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserDataController {

    private final UserService userService;
    private final MysticSchoolClient mysticSchoolClient;
    private final ModelMapperService mapperService;

    @PostMapping("/user/add/")
    public ResponseEntity<ApiResponse<UserDTO>> save(
            @RequestBody ApiRequest<UserDTO> request
    ) {
        log.debug("Запрос на сохранения User {}", request);

        UserDTO dto = request.getData();

        if (Objects.isNull(dto)) throw new EntityNullException(
                ExceptionMessageProvider.getEntityNullExceptionText(new User())
        );

        User user = mapperService.toEntity(dto, User.class);

        ApiResponse<UserDTO> result = ApiResponse.<UserDTO>builder().build();

        if (Objects.nonNull(user)) result = userService.save(user);

        return ResponseEntity.status(result.getStatus()).body(result);
    }
    @PostMapping("/user/update/")
    public ResponseEntity<ApiResponse<UserDTO>> update(
            @RequestBody ApiRequest<UserDTO> request
    ) {
        log.debug("Запрос на обновление User {}", request);

        UserDTO dto = request.getData();
        User user = mapperService.toEntity(dto, User.class);

        ApiResponse<UserDTO> result = ApiResponse.<UserDTO>builder().build();

        if (Objects.nonNull(user)) result = userService.update(user);

        return ResponseEntity.status(result.getStatus()).body(result);
    }
    @GetMapping("/user/id/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получение User по id={}", id);

        ApiResponse<UserDTO> response = userService.getUserById(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/user/username/{username}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(
            @PathVariable("username") String username
    ) {

        log.debug("Запрос на получение User по username {}", username);

        ApiResponse<UserDTO> response = userService.getUserByUsername(username);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/email/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(
            @PathVariable("email") String email
    ) {

        log.debug("Запрос на получение User по email {}", email);

        ApiResponse<UserDTO> response = userService.getUserByEmail(email);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByTelegramUserId(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получение User по telegramUserId {}", id);

        ApiResponse<UserDTO> response = userService.getUserByTelegramUserId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/user_details/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByTelegramUserIdWithUserDetails(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получение User по telegramUserId {}", id);

        ApiResponse<UserDTO> response = userService.getUserByTelegramUserIdWithUserDetails(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/telegram_user/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserWithTelegramUserByTelegramUserId(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получение User по telegramUserId {}", id);

        ApiResponse<UserDTO> response = userService.getUserByTelegramUserIdWithTelegramUser(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/full/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByTelegramUserIdFull(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получение User full по telegramUserId {}", id);

        ApiResponse<UserDTO> response = userService.getUserByTelegramUserIdFull(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/check/auth/{id}")
    public Mono<ResponseEntity<ApiResponse>> checkUserAuthenticationInMysticSchoolDbByTgUserId(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на проверку авторизации юзера с id {}", id);

        return Mono.just(userService.getUserByTelegramUserId(id))
                .flatMap(response -> {
                    if (!isNull(response.getData())) {
                        User user = mapperService.toEntity(response.getData(), User.class);
                        return mysticSchoolClient.checkUserAuthentication(user.getEmail());
                    }

                    return Mono.just(CheckUserResponse.builder()
                            .found(false)
                            .build());
                })
                .map(mysticSchoolResponse -> {

                    log.debug(mysticSchoolResponse.toString());

                    return ResponseEntity.status(HttpStatus.OK).body(
                            success(mysticSchoolResponse)
                    );
                });
    }

    @GetMapping("/user/check/auth/")
    public Mono<ResponseEntity<CheckUserResponse>> checkUserAuthenticationInMysticSchoolDbByUserEmail(
            @RequestParam("email") String email
    ) {

        log.debug("Запрос на проверку авторизации юзера с email {}", email);

        return mysticSchoolClient.checkUserAuthentication(email)
                .flatMap(mysticSchoolResponse -> {

                    log.debug(mysticSchoolResponse.toString());

                    return Mono.just(ResponseEntity.status(HttpStatus.OK).body(mysticSchoolResponse));

                });
    }

    @PostMapping("/user/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteUser(
            @PathVariable(name = "id") Long id
    ) {
        ApiResponse<Boolean> response = userService.delete(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
