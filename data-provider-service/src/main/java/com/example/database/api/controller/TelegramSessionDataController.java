package com.example.database.api.controller;

import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.database.entity.TelegramSession;
import com.example.database.entity.TelegramUser;
import com.example.database.service.ModelMapperService;
import com.example.database.service.telegram.TelegramSessionService;
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
public class TelegramSessionDataController {

    private final TelegramSessionService sessionService;
    private final ModelMapperService mapperService;
    private final ObjectMapper objectMapper;

    @PostMapping(path = {"/session/add/", "/session/add"})
    public ResponseEntity<ApiResponse<TelegramSessionDTO>> addSession(
            @RequestBody ApiRequest<TelegramSessionDTO> request
    ) {
        log.debug("Запрос на сохранение TelegramSession {}", request);

        TelegramSessionDTO dto = request.getData();

        TelegramSession telegramSessionForSave = mapperService.toEntity(dto, TelegramSession.class);
        TelegramUser telegramUser = objectMapper.convertValue(
                request.getIncludeObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue()),
                TelegramUser.class
        );

        telegramSessionForSave.setTelegramUser(telegramUser);

        log.debug("Telegram user = {}", telegramUser);
        log.debug("Telegram session = {}", telegramSessionForSave);

        ApiResponse<TelegramSessionDTO> response = sessionService.save(telegramSessionForSave);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = {"/session/update/", "/session/update"})
    public ResponseEntity<ApiResponse<TelegramSessionDTO>> updateSession(
            @RequestBody ApiRequest<TelegramSessionDTO> request
    ) {
        log.debug("Запрос на обновление TelegramSession {}", request);

        TelegramSessionDTO dto = request.getData();

        TelegramSession telegramSessionForUpdate = mapperService.toEntity(dto, TelegramSession.class);
        TelegramUser telegramUser = objectMapper.convertValue(
                request.getIncludeObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue()),
                TelegramUser.class
        );

        telegramSessionForUpdate.setTelegramUser(telegramUser);

        ApiResponse<TelegramSessionDTO> response = sessionService.update(telegramSessionForUpdate);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/session/{id}"})
    public ResponseEntity<ApiResponse<TelegramSessionDTO>> getSessionById(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение TelegramSession по ID {}", id);

        ApiResponse<TelegramSessionDTO> response = sessionService.getSessionById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/session/telegram_user_id/{telegramUserId}"})
    public ResponseEntity<ApiResponse<TelegramSessionDTO>> getSessionByTelegramUserId(
            @PathVariable(name = "telegramUserId") Long telegramUserId
    ) {

        log.debug("Запрос на получение TelegramSession по telegramUserId {}", telegramUserId);

        ApiResponse<TelegramSessionDTO> response = sessionService.getByTelegramUserIdWithTelegramUser(telegramUserId);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping("/session/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteSession(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на удаление TelegramSession с id {}", id);

        ApiResponse<Boolean> response = sessionService.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}