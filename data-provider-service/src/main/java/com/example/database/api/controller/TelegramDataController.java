package com.example.database.api.controller;

import com.example.data.models.entity.dto.mysticschool.ArticleCategory;
import com.example.data.models.entity.dto.response.ApiResponseWithDataList;
import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.data.models.entity.dto.request.ApiRequest;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.response.CheckUserResponse;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.database.api.client.MysticSchoolClient;
import com.example.database.entity.TelegramChat;
import com.example.database.service.telegram.TelegramChatsService;
import com.example.database.service.telegram.TelegramSessionService;
import com.example.database.service.telegram.TelegramUserService;
import com.example.database.service.telegram.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Log4j2
public class TelegramDataController {

    private final UserService userService;

    private final TelegramChatsService chatsService;

    private final TelegramUserService telegramUserService;

    private final TelegramSessionService sessionService;

    private final MysticSchoolClient mysticSchoolClient;


    @GetMapping("/user/check/auth/{id}")
    public Mono<ResponseEntity<ApiResponse<CheckUserResponse>>> checkUserAuthentication(
            @PathVariable("id") Long id
    ) {

        log.debug("Входящий запрос на проверку авторизации юзера с id {}", id);

        return Mono.just(userService.getUserByTelegramUserId(id))
                .flatMap(user -> mysticSchoolClient.checkUserAuthentication(user.getEmail()))
                .map(mysticSchoolResponse -> {

                    log.debug(mysticSchoolResponse);

                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ApiResponse<>(HttpStatus.OK, HttpStatus.OK.toString(), mysticSchoolResponse)
                    );
                })
                .defaultIfEmpty(
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "User not found"))
                )  // Handle empty user
                .onErrorResume(e -> { // Handle errors
                    log.error("Error during user check", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())
                    ));
                });
    }

    @GetMapping("/telegram/user/get/{id}")
    public ResponseEntity<ApiResponse<TelegramUserDTO>> getTelegramUserById(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение TelegramUser по id {}", id);

        ApiResponse<TelegramUserDTO> response = telegramUserService.getUserById(id);

        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @GetMapping("/chat/get/{id}")
    public ResponseEntity<ApiResponse<TelegramChatDTO>> getChats(
            @PathVariable("id") Long chatId
            ) {

        log.debug("Входящий запрос на получения чатов по id {}", chatId);

        ApiResponse<TelegramChatDTO> response = chatsService.getTelegramChatById(chatId);

        log.debug("Ответ {}", response);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {"/chat/add/", "/chat/add"})
    public ResponseEntity<ApiResponse<TelegramChatDTO>> addChat(
            @RequestBody ApiRequest<TelegramChatDTO> request
            ) {

        log.debug("Входящий запрос на сохранение чата {}", request);

        TelegramChatDTO data = request.getData();

        ApiResponse<TelegramUserDTO> telegramUserDTO = telegramUserService.getUserById(data.getTelegramUserId());

        TelegramChat chat = chatsService.toEntity(data);

        chat.setTelegramUser(telegramUserService.toEntity(telegramUserDTO.getData()));

        ApiResponse<TelegramChatDTO> response = chatsService.create(chat);

        log.debug("Ответ {}", response);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/session/get/{id}"})
    public ResponseEntity<ApiResponse<TelegramSessionDTO>> getSessionById(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение TelegramSession по ID {}", id);

        ApiResponse<TelegramSessionDTO> response = sessionService.getSessionById(id);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(path = {"/session/add", "/session/add"})
    public ResponseEntity<ApiResponse<TelegramSessionDTO>> addSession(
            @RequestBody ApiRequest<TelegramSessionDTO> request
    ) {
        log.debug("Запрос на сохранение TelegramSession {}", request);

        ApiResponse<TelegramSessionDTO> response = sessionService.create(
                sessionService.toEntity(request.getData())
        );

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(path = {"/session/get/telegramUserId/{telegramUserId}"})
    public ResponseEntity<ApiResponse<TelegramSessionDTO>> getSessionByTelegramUserId(
            @PathVariable(name = "telegramUserId") Long telegramUserId
    ) {

        log.debug("Запрос на получение TelegramSession по telegramUserId {}", telegramUserId);

        ApiResponse<TelegramSessionDTO> response = sessionService.findByTelegramUserId(telegramUserId);

        return ResponseEntity.status(response.getStatus()).body(response);

    }

    //Секция обработки запросов получения материалов с сайта Школы
    @GetMapping("/articles/category/")
    public Mono<ResponseEntity<ApiResponseWithDataList>> getArticleCategories(
            @RequestParam("id") int id
    ) {
        return mysticSchoolClient.getArticleCategoryById(id)
                .map(resp ->  {

                    ApiResponseWithDataList<ArticleCategory> articleCategoryApiResponse =
                            new ApiResponseWithDataList<>(HttpStatus.OK, "Success!", resp);

                    return ResponseEntity.status(HttpStatus.OK).body(articleCategoryApiResponse);
                });
    }

    @GetMapping("/articles/category/getAll")
    public Mono<ResponseEntity<ApiResponseWithDataList<ArticleCategory>>> getArticleCategories() {
        return mysticSchoolClient.getArticleCategories()
                .map(resp ->  {

                    ApiResponseWithDataList<ArticleCategory> articleCategoryApiResponseWithDataList =
                            new ApiResponseWithDataList<>(HttpStatus.OK, "Success!", resp);

                    return ResponseEntity.status(HttpStatus.OK).body(articleCategoryApiResponseWithDataList);
                });
    }

}
