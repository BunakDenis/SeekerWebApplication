package com.example.database.api.controller;

import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.mysticschool.ArticleCategory;
import com.example.data.models.entity.dto.response.ApiResponseWithDataList;
import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.data.models.entity.dto.request.ApiRequest;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.response.CheckUserResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.database.api.client.MysticSchoolClient;
import com.example.database.entity.*;
import com.example.database.service.ModelMapperService;
import com.example.database.service.UserService;
import com.example.database.service.telegram.*;
import com.example.utils.text.ExceptionServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.example.data.models.utils.ApiResponseUtilsService.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TelegramDataController {

    private final UserService userService;
    private final VerificationCodeService verificationCodeService;
    private final TelegramChatsService chatsService;
    private final TelegramUserService telegramUserService;
    private final TelegramSessionService sessionService;
    private final MysticSchoolClient mysticSchoolClient;
    private final ModelMapperService mapperService;

    @GetMapping("/user/get/id/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получение User по {}", id);

        ApiResponse<UserDTO> response = userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/get/username/{username}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(
            @PathVariable("username") String username
    ) {

        log.debug("Запрос на получение User по username {}", username);

        ApiResponse<UserDTO> response = userService.getUserByUsername(username);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/get/email/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(
            @PathVariable("email") String email
    ) {

        log.debug("Запрос на получение User по email {}", email);

        ApiResponse<UserDTO> response = userService.getUserByEmail(email);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/get/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByTelegramUserId(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получение User по telegramUserId {}", id);

        ApiResponse<UserDTO> response = userService.getUserByTelegramUserId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/user_details/get/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByTelegramUserIdWithUserDetails(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получение User по telegramUserId {}", id);

        ApiResponse<UserDTO> response = userService.getUserByTelegramUserIdWithUserDetails(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/telegram_user/get/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByTelegramUserIdWithTelegramUser(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получение User по telegramUserId {}", id);

        ApiResponse<UserDTO> response = userService.getUserByTelegramUserIdWithTelegramUser(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/full/get/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByTelegramUserIdFull(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на получение User по telegramUserId {}", id);

        ApiResponse<UserDTO> response = userService.getUserByTelegramUserIdFull(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/check/auth/{id}")
    public Mono<ResponseEntity<ApiResponse>> checkUserAuthentication(
            @PathVariable("id") Long id
    ) {

        log.debug("Запрос на проверку авторизации юзера с id {}", id);

        return Mono.just(userService.getUserByTelegramUserId(id))
                .flatMap(response -> {
                    User user = mapperService.toEntity(response.getData(), User.class);
                    return mysticSchoolClient.checkUserAuthentication(user.getEmail());
                })
                .map(mysticSchoolResponse -> {

                    log.debug(mysticSchoolResponse.toString());

                    return ResponseEntity.status(HttpStatus.OK).body(
                            success(mysticSchoolResponse)
                    );
                });
    }

    @GetMapping("/otp_code/get/{id}")
    public ResponseEntity<ApiResponse<VerificationCodeDTO>> getVerificationCodeById(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение VerificationCode по id {}", id);

        ApiResponse<VerificationCodeDTO> response = verificationCodeService.getCodeById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/otp_code/get/user_id/{id}")
    public ResponseEntity<ApiResponse<VerificationCodeDTO>> getVerificationCodeByUserId(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение VerificationCode по user_id {}", id);

        ApiResponse<VerificationCodeDTO> response = verificationCodeService.getCodeByUserId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/otp_code/get/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<VerificationCodeDTO>> getVerificationCodeByTelegramUserId(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение VerificationCode по user_id {}", id);

        ApiResponse<VerificationCodeDTO> response = verificationCodeService.getCodeByUserId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/otp_code/add/")
    public ResponseEntity<ApiResponse<VerificationCodeDTO>> saveVerificationCode(
            @RequestBody ApiRequest<VerificationCodeDTO> request
    ) {
        log.debug("Запрос на сохранения VerificationCode {}", request);

        VerificationCode verificationCode = mapperService.toEntity(request.getData(), VerificationCode.class);

        User user = mapperService.toEntity(
                request.getIncludeObject("user"),
                User.class
        );

        verificationCode.setUser(user);

        ApiResponse<VerificationCodeDTO> response = verificationCodeService.save(verificationCode);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/otp_code/update/")
    public ResponseEntity<ApiResponse<VerificationCodeDTO>> updateVerificationCode(
            @RequestBody ApiRequest<VerificationCodeDTO> request
    ) {
        log.debug("Запрос на обновление VerificationCode {}", request);

        ApiResponse<VerificationCodeDTO> response = verificationCodeService.update(
                mapperService.toEntity(request.getData(), VerificationCode.class)
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/otp_code/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteVerificationCode(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на удаление VerificationCode с id {}", id);

        ApiResponse<Boolean> response = verificationCodeService.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/telegram/user/get/{id}")
    public ResponseEntity<ApiResponse<TelegramUserDTO>> getTelegramUserById(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение TelegramUser по id {}", id);

        ApiResponse<TelegramUserDTO> response = telegramUserService.getUserById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping("/chat/get/{id}")
    public ResponseEntity<ApiResponse<TelegramChatDTO>> getChats(
            @PathVariable("id") Long chatId
            ) {

        log.debug("Запрос на получения чатов по id {}", chatId);

        ApiResponse<TelegramChatDTO> response = chatsService.getTelegramChatById(chatId);

        log.debug("Ответ {}", response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/chat/telegram_user/get/{id}")
    public ResponseEntity<ApiResponse<TelegramChatDTO>> getChatsWithTelegramUser(
            @PathVariable("id") Long chatId
    ) {

        log.debug("Запрос на получения чатов с телеграм юзером по id {}", chatId);

        ApiResponse<TelegramChatDTO> response = chatsService.getTelegramChatByIdWithTelegramUser(chatId);

        log.debug("Ответ {}", response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = {"/chat/add/", "/chat/add"})
    public ResponseEntity<ApiResponse<TelegramChatDTO>> addChat(
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

    @GetMapping(path = {"/session/get/{id}"})
    public ResponseEntity<ApiResponse<TelegramSessionDTO>> getSessionById(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение TelegramSession по ID {}", id);

        ApiResponse<TelegramSessionDTO> response = sessionService.getSessionById(id);

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

    @PostMapping("/session/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteSession(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на удаление TelegramSession с id {}", id);

        ApiResponse<Boolean> response = sessionService.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/session/get/telegramUserId/{telegramUserId}"})
    public ResponseEntity<ApiResponse<TelegramSessionDTO>> getSessionByTelegramUserId(
            @PathVariable(name = "telegramUserId") Long telegramUserId
    ) {

        log.debug("Запрос на получение TelegramSession по telegramUserId {}", telegramUserId);

        ApiResponse<TelegramSessionDTO> response = sessionService.findByTelegramUserId(telegramUserId);

        return ResponseEntity.status(HttpStatus.OK).body(response);

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
