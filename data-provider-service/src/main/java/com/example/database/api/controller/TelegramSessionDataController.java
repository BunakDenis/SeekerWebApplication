package com.example.database.api.controller;


import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.telegram.TelegramSessionDTO;
import com.example.database.entity.TelegramSession;
import com.example.database.service.ModelMapperService;
import com.example.database.service.telegram.TelegramSessionService;
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

    @PostMapping(path = {"/session/add", "/session/add"})
    public ResponseEntity<ApiResponse<TelegramSessionDTO>> addSession(
            @RequestBody ApiRequest<TelegramSessionDTO> request
    ) {
        log.debug("Запрос на сохранение TelegramSession {}", request);

        ApiResponse<TelegramSessionDTO> response = sessionService.create(
                mapperService.toEntity(request.getData(), TelegramSession.class)
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = {"/session/update/", "/session/update"})
    public ResponseEntity<ApiResponse<TelegramSessionDTO>> updateSession(
            @RequestBody ApiRequest<TelegramSessionDTO> request
    ) {
        log.debug("Запрос на обновление TelegramSession {}", request);

        ApiResponse<TelegramSessionDTO> response = sessionService.update(
                mapperService.toEntity(request.getData(), TelegramSession.class)
        );

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

        ApiResponse<TelegramSessionDTO> response = sessionService.getByTelegramUserId(telegramUserId);

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
