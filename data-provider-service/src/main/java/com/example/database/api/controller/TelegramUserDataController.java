package com.example.database.api.controller;


import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.database.entity.TelegramUser;
import com.example.database.entity.User;
import com.example.database.service.ModelMapperService;
import com.example.database.service.telegram.TelegramUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TelegramUserDataController {

    private final TelegramUserService telegramUserService;
    private final ModelMapperService mapperService;
    private final ObjectMapper objectMapper;

    @PostMapping("/telegram_user/add/")
    public ResponseEntity<ApiResponse<TelegramUserDTO>> save(
            @RequestBody ApiRequest<TelegramUserDTO> request
            ) {

        TelegramUserDTO dto = request.getData();

        TelegramUser telegramUser = mapperService.toEntity(dto, TelegramUser.class);
        User user = objectMapper.convertValue(request.getIncludeObject(ResponseIncludeDataKeys.USER.getKeyValue()), User.class);

        telegramUser.setUser(user);

        ApiResponse<TelegramUserDTO> result = telegramUserService.save(telegramUser);

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping("/telegram_user/update/")
    public ResponseEntity<ApiResponse<TelegramUserDTO>> update(
            @RequestBody ApiRequest<TelegramUserDTO> request
    ) {
        TelegramUser telegramUser = mapperService.toEntity(request.getData(), TelegramUser.class);

        ApiResponse<TelegramUserDTO> result = telegramUserService.update(telegramUser);

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping("/telegram_user/{id}")
    public ResponseEntity<ApiResponse<TelegramUserDTO>> getTelegramUserById(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение TelegramUser по id {}", id);

        ApiResponse<TelegramUserDTO> response = telegramUserService.getByTelegramUserId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping("/telegram_user/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> delete(
            @PathVariable("id") Long id
    ) {

        ApiResponse<Boolean> response = telegramUserService.delete(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }


}
