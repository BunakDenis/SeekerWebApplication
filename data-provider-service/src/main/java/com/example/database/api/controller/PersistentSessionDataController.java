package com.example.database.api.controller;

import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.database.entity.PersistentSession;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.PersistentSessionDTO;
import com.example.database.entity.TelegramSession;
import com.example.database.service.ModelMapperService;
import com.example.database.service.telegram.PersistentSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class PersistentSessionDataController {

    private final PersistentSessionService sessionService;
    private final ModelMapperService mapperService;

    @PostMapping({"/persistent_session/add/", "/persistent_session/add"})
    public ResponseEntity<ApiResponse<PersistentSessionDTO>> save(
            @RequestBody ApiRequest<PersistentSessionDTO> request
    ) {

        log.debug("Запрос на сохранения PersistentSession {}", request);

        PersistentSession persistentSession = mapperService.toEntity(request.getData(), PersistentSession.class);
        TelegramSession telegramSession = mapperService.toEntity(
                request.getIncludeObject(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue()),
                TelegramSession.class
        );

        persistentSession.setTelegramSession(telegramSession);

        ApiResponse<PersistentSessionDTO> response = sessionService.save(persistentSession);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping({"/persistent_session/update/", "/persistent_session/update"})
    public ResponseEntity<ApiResponse<PersistentSessionDTO>> update (
            @RequestBody ApiRequest<PersistentSessionDTO> request
    ) {

        PersistentSession persistentSession = mapperService.toEntity(request.getData(), PersistentSession.class);
        TelegramSession telegramSession = mapperService.toEntity(
                request.getIncludeObject(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue()),
                TelegramSession.class
        );

        persistentSession.setTelegramSession(telegramSession);

        ApiResponse<PersistentSessionDTO> response =
                sessionService.update(mapperService.toEntity(persistentSession, PersistentSession.class));

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/persistent_session/{id}")
    public ResponseEntity<ApiResponse<PersistentSessionDTO>> getById(
            @PathVariable("id") Long id
    ) {
        ApiResponse<PersistentSessionDTO> response = sessionService.getById(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/persistent_session/active/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<PersistentSessionDTO>> getActiveByTgUserId (
            @PathVariable("id") Long id
    ) {
        ApiResponse<PersistentSessionDTO> response = sessionService.getActiveByTelegramUserId(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/persistent_session/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> delete(
            @PathVariable("id") Long id
    ) {
        ApiResponse<Boolean> response = sessionService.delete(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
