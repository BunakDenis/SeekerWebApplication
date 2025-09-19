package com.example.database.api.controller;


import com.example.data.models.entity.dto.request.ApiRequest;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.database.entity.TelegramChat;
import com.example.database.entity.TelegramUser;
import com.example.database.service.ModelMapperService;
import com.example.database.service.telegram.TelegramChatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TelegramChatDataController {

    private final TelegramChatsService chatsService;
    private final ModelMapperService mapperService;


    @PostMapping(path = {"/chat/add/", "/chat/add"})
    public ResponseEntity<ApiResponse<TelegramChatDTO>> save(
            @RequestBody ApiRequest<TelegramChatDTO> request
    ) {

        log.debug("Запрос на сохранение чата {}", request);

        TelegramChatDTO data = request.getData();

        TelegramChat chat = mapperService.toEntity(data, TelegramChat.class);
        TelegramUser telegramUser = mapperService.toEntity(
                request.getIncludeObject("telegram_user"), TelegramUser.class
        );

        chat.setTelegramUser(telegramUser);

        ApiResponse<TelegramChatDTO> response = chatsService.create(chat);

        log.debug("Ответ {}", response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping(path = {"/chat/update/", "/chat/updates"})
    public ResponseEntity<ApiResponse<TelegramChatDTO>> update(
            @RequestBody ApiRequest<TelegramChatDTO> request
    ) {

        log.debug("Запрос на обновление чата {}", request);

        TelegramChatDTO data = request.getData();

        TelegramChat chat = mapperService.toEntity(data, TelegramChat.class);

        ApiResponse<TelegramChatDTO> response = chatsService.create(chat);

        log.debug("Ответ {}", response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/chat/{id}")
    public ResponseEntity<ApiResponse<TelegramChatDTO>> getById(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получения чатов по id {}", id);

        ApiResponse<TelegramChatDTO> response = chatsService.getTelegramChatById(id);

        log.debug("Ответ {}", response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/chat/telegram_user/{id}")
    public ResponseEntity<ApiResponse<TelegramChatDTO>> getWithTelegramUser(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получения чатов с телеграм юзером по id {}", id);

        ApiResponse<TelegramChatDTO> response = chatsService.getTelegramChatByTelegramUserIdWithTelegramUser(id);

        log.debug("Ответ {}", response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/chat/telegram_user_id/{telegram_user_id}")
    public ResponseEntity<ApiResponse<TelegramChatDTO>> getLastByTelegramUserId(
            @PathVariable("telegram_user_id") Long id
    ) {

        log.debug("Запрос на получения чатов по telegram_user_id {}", id);

        ApiResponse<TelegramChatDTO> response = chatsService.getTelegramChatByTelegramUserIdWithTelegramUser(id);

        log.debug("Ответ {}", response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/chat/delete/")
    public ResponseEntity<ApiResponse<Boolean>> delete(
            @PathVariable("id") Long id
    ) {

        ApiResponse<Boolean> response = chatsService.delete(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
