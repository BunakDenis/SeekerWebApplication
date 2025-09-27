package com.example.database.api.controller;


import com.example.data.models.entity.dto.telegram.PersistentSessionDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.database.entity.TelegramSession;
import com.example.database.entity.TransientSession;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TransientSessionDTO;
import com.example.database.service.ModelMapperService;
import com.example.database.service.telegram.TransientSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TransientSessionDataController {

    private final TransientSessionService sessionService;
    private final ModelMapperService mapperService;
    private final ObjectMapper objectMapper;

    @PostMapping({"/transient_session/add/", "/transient_session/add"})
    public ResponseEntity<ApiResponse<TransientSessionDTO>> save(
            @RequestBody ApiRequest<TransientSessionDTO> request
    ) {

        log.debug("Запрос на сохранение TransientSession {}", request);

        TransientSession transientSession = mapperService.toEntity(request.getData(), TransientSession.class);
        TelegramSession telegramSession = mapperService.toEntity(
                request.getIncludeObject(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue()),
                TelegramSession.class
        );

        transientSession.setTelegramSession(telegramSession);

        ApiResponse<TransientSessionDTO> response = sessionService.save(transientSession);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping({"/transient_session/update/", "/transient_session/update"})
    public ResponseEntity<ApiResponse<TransientSessionDTO>> update (
            @RequestBody ApiRequest<TransientSessionDTO> request
    ) {

        TransientSession transientSession = mapperService.toEntity(request.getData(), TransientSession.class);
        TelegramSession telegramSession = mapperService.toEntity(
                request.getIncludeObject(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue()),
                TelegramSession.class
        );

        transientSession.setTelegramSession(telegramSession);

        ApiResponse<TransientSessionDTO> response =
                sessionService.update(transientSession);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/transient_session/{id}")
    public ResponseEntity<ApiResponse<TransientSessionDTO>> getById(
            @PathVariable("id") Long id
    ) {
        ApiResponse<TransientSessionDTO> response = sessionService.getById(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/transient_session/active/{id}")
    public ResponseEntity<ApiResponse<TransientSessionDTO>> getActiveById(
            @PathVariable("id") Long id
    ) {
        ApiResponse<TransientSessionDTO> response = sessionService.getActiveById(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/transient_session/active/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<TransientSessionDTO>> getActiveByTgUserId (
            @PathVariable("id") Long id
    ) {
        ApiResponse<TransientSessionDTO> response = sessionService.getActiveByTelegramUserId(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/transient_session/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> delete(
            @PathVariable("id") Long id
    ) {
        ApiResponse<Boolean> response = sessionService.delete(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
