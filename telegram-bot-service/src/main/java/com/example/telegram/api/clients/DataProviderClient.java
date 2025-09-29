package com.example.telegram.api.clients;


import com.example.data.models.entity.*;
import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.dto.telegram.*;
import com.example.data.models.entity.response.CheckUserResponse;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.exception.ApiException;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.telegram.bot.service.ModelMapperService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import static com.example.data.models.consts.DataProviderEndpointsConsts.*;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataProviderClient {


    @Value("${data.provide.api.url}")
    private String dataProviderURL;
    @Value("${data.provide.api.version}")
    private String apiVersion;
    @Value("${api.key.header.name}")
    private String apiKeyHeaderName;
    @Value("${telegram.api.key}")
    private String apiKey;
    private WebClient webClient;
    private final ModelMapperService mapperService;

    @PostConstruct
    public void init() {

        String baseURL = dataProviderURL + apiVersion;

        log.debug("Устанавливаем baseUrl для Data provider client = " + baseURL);

        this.webClient = WebClient.builder()
                .baseUrl(baseURL)
                .defaultHeader(apiKeyHeaderName, apiKey)
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    log.debug("Request method: {}", clientRequest.method());
                    log.debug("Request URL: {}", clientRequest.url());
                    return Mono.just(clientRequest);
                }))
                .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    log.debug("Response status code: {}", clientResponse.statusCode());
                    return Mono.just(clientResponse);
                }))
                .build();
    }

    //USER SECTION
    public Mono<ApiResponse<UserDTO>> createUser(UserDTO dto) {
        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("add/"));

        ApiRequest<UserDTO> request = new ApiRequest<>(dto);

        return webClient.post()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<UserDTO>> updateUser(UserDTO dto) {
        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("update/"));

        ApiRequest<UserDTO> request = new ApiRequest<>(dto);

        return webClient.post()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<UserDTO>> getUserById(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("id/"));
        endpoint.append(id);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<UserDTO>> getUserByUsername(String username) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("username/"));
        endpoint.append(username);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<UserDTO>> getUserByEmail(String email) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("email/"));
        endpoint.append(email);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserId(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("telegram_user_id/"));
        endpoint.append(id);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserIdWithUserDetails(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("user_details/telegram_user_id/"));
        endpoint.append(id);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserIdWithTelegramUser(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("telegram_user/telegram_user_id/"));
        endpoint.append(id);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserIdFull(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("full/telegram_user_id/"));
        endpoint.append(id);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<CheckUserResponse>> checkUserAuthInMysticSchoolDbByTgUserId(Long telegramUserId) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("check/auth/"));
        endpoint.append(telegramUserId);

        return webClient.get()
                .uri(builder -> builder.path(endpoint.toString()).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<CheckUserResponse>> checkUserAuthInMysticSchoolDbByUserEmail(String email) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("check/auth/"));

        return webClient.get()
                .uri(builder -> builder.path(endpoint.toString()).queryParam("email", email).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<Boolean>> deleteUserById(Long id) {
        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("delete/"));
        endpoint.append(id);

        return webClient.post()
                .uri(endpoint.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    //TELEGRAM USER SECTION
    public Mono<ApiResponse<TelegramUserDTO>> createTelegramUser(TelegramUser telegramUser) {

        TelegramUserDTO dto = mapperService.toDTO(telegramUser, TelegramUserDTO.class);
        UserDTO userDTO = mapperService.toDTO(telegramUser.getUser(), UserDTO.class);

        StringBuilder endpoint = new StringBuilder(getApiTelegramUserEndpoint("add/"));

        ApiRequest<?> request = new ApiRequest(dto);

        request.addIncludeObject(ResponseIncludeDataKeys.USER.getKeyValue(), userDTO);

        return webClient.post()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TelegramUserDTO>> updateTelegramUser(TelegramUserDTO dto) {

        StringBuilder endpoint = new StringBuilder(getApiTelegramUserEndpoint("update/"));

        ApiRequest request = new ApiRequest(dto);

        return webClient.post()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TelegramUserDTO>> getTelegramUserByTelegramUserId(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiTelegramUserEndpoint(""));
        endpoint.append(id);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TelegramUserDTO>> getTelegramUserByTelegramUserIdWithTelegramSession(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiTelegramUserEndpoint("telegram_session/"));
        endpoint.append(id);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.empty();
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<Boolean>> deleteTelegramUser(Long id) {

        return webClient.post()
                .uri(getApiTelegramUserEndpoint("delete/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    //TELEGRAM CHAT SECTION
    public Mono<ApiResponse<TelegramChatDTO>> saveTelegramChat(TelegramChat chat) {

        StringBuilder endpoint = new StringBuilder(getApiChatEndpoint("add/"));

        TelegramChatDTO dto = mapperService.toDTO(chat, TelegramChatDTO.class);
        TelegramUserDTO telegramUserDTO = mapperService.toDTO(chat.getTelegramUser(), TelegramUserDTO.class);

        ApiRequest<TelegramChatDTO> request = new ApiRequest(dto);

        request.addIncludeObject("telegram_user", telegramUserDTO);

        log.debug("Отправляю запрос к Data provide service для записи чата {}", dto);
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.post().uri(endpoint.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ с ошибкой от Data provider service {}", errorBody);
                                    return Mono.error(new RuntimeException(
                                            String.format("Error from Data provider service: Status %d, Body: %s",
                                                    response.statusCode().value(), errorBody)));
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TelegramChatDTO>> updateTelegramChat(TelegramChatDTO dto) {

        StringBuffer endpoint = new StringBuffer(getApiChatEndpoint("update"));
        ApiRequest<TelegramChatDTO> request = new ApiRequest<>(dto);

        log.debug("Отправляю запрос к Data provide service для обновления информации о чате {}", dto);
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.post()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TelegramChatDTO>> getTelegramChat(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiChatEndpoint(""));
        endpoint.append(id);

        log.debug("Отправляю запрос к Data provide service для получения чата {}", id);
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.get().uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service {}", errorBody);
                                    return Mono.error(new ApiException(
                                            String.format("Error from Data provider service: Status %d, Body: %s",
                                                    response.statusCode().value(), errorBody)));
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TelegramChatDTO>> getTelegramChatByIdWithTgUser(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiChatEndpoint("telegram_user/"));
        endpoint.append(id);

        log.debug("Отправляю запрос к Data provide service для получения чата с телеграм юзером {}", id);
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.get()
                .uri(builder ->
                        builder.path(endpoint.toString())
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response ->
                                response.bodyToMono(String.class).flatMap(body -> {
                                            log.error("Ответ от Data provide service \n{}", body);
                                            return Mono.empty();
                                        }
                                )
                )
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TelegramChatDTO>> getTelegramChatByTelegramUserId(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiChatEndpoint("telegram_user_id/"));
        endpoint.append(id);

        log.debug("Отправляю запрос к Data provide service для получения чата с телеграм юзером {}", id);
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.get()
                .uri(endpoint.toString()).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<Boolean>> deleteTelegramChat(Long id) {
        return webClient.post()
                .uri(getApiChatEndpoint("delete/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    //TELEGRAM SESSION SECTION
    public Mono<ApiResponse<TelegramSessionDTO>> saveTelegramSession(TelegramSession session) {

        TelegramSessionDTO dto = mapperService.toDTO(session, TelegramSessionDTO.class);
        TelegramUserDTO telegramUserDTO = mapperService.toDTO(session.getTelegramUser(), TelegramUserDTO.class);

        ApiRequest<TelegramSessionDTO> request = new ApiRequest<>(dto);

        request.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(), telegramUserDTO);

        StringBuilder endpoint = new StringBuilder(getApiSessionEndpoint("add/"));

        log.debug("Отправляю запрос к Data provide service на сохранение TelegramSession");
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.post()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TelegramSessionDTO>> getTelegramSessionByTelegramUserId(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiSessionEndpoint("telegram_user_id/"));
        endpoint.append(id);

        log.debug("Отправляю запрос к Data provide service для получения TelegramSession по telegram_user_id {}", id);
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TransientSessionDTO>> saveTransientSession(TransientSession session) {

        log.debug("Запрос на сохранение TransientSession {}", session);
        log.debug("Telegram Session {}", session.getTelegramSession());


        TransientSessionDTO dto = mapperService.toDTO(session, TransientSessionDTO.class);
        TelegramSessionDTO telegramSessionDTO =
                mapperService.toDTO(session.getTelegramSession(), TelegramSessionDTO.class);

        ApiRequest<TransientSessionDTO> request = new ApiRequest<>(dto);

        request.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue(), telegramSessionDTO);

        return webClient.post()
                .uri(getApiTransientSessionEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TransientSessionDTO>> updateTransientSession(TransientSession session) {

        TransientSessionDTO dto = mapperService.toDTO(session, TransientSessionDTO.class);
        TelegramSessionDTO telegramSessionDTO = mapperService.toDTO(session.getTelegramSession(), TelegramSessionDTO.class);

        ApiRequest<TransientSessionDTO> request = new ApiRequest<>(dto);

        request.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue(), telegramSessionDTO);

        return webClient.post()
                .uri(getApiTransientSessionEndpoint("update/"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<Boolean>> deleteTransientSession(Long id) {

        return webClient.post()
                .uri(getApiTransientSessionEndpoint("delete/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<PersistentSessionDTO>> savePersistentSession(PersistentSession session) {

        log.debug("Запрос на сохранение Persistent Session {}", session);
        log.debug("Telegram Session {}", session.getTelegramSession());

        PersistentSessionDTO dto = mapperService.toDTO(session, PersistentSessionDTO.class);
        TelegramSessionDTO telegramSessionDTO = mapperService.toDTO(session.getTelegramSession(), TelegramSessionDTO.class);

        ApiRequest<PersistentSessionDTO> request = new ApiRequest<>(dto);

        request.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue(), telegramSessionDTO);

        return webClient.post()
                .uri(getApiPersistentSessionEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<PersistentSessionDTO>> updatePersistentSession(PersistentSession session) {

        PersistentSessionDTO dto = mapperService.toDTO(session, PersistentSessionDTO.class);
        TelegramSessionDTO telegramSessionDTO = mapperService.toDTO(session.getTelegramSession(), TelegramSessionDTO.class);

        ApiRequest<PersistentSessionDTO> request = new ApiRequest<>(dto);

        request.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue(), telegramSessionDTO);

        return webClient.post()
                .uri(getApiPersistentSessionEndpoint("update/"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<Boolean>> deletePersistentSession(Long id) {

        return webClient.post()
                .uri(getApiPersistentSessionEndpoint("delete/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    //VERIFICATION CODE SECTION
    public Mono<ApiResponse<VerificationCodeDTO>> getVerificationCodeById(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint(Long.toString(id)));
        endpoint.append(id);

        log.debug("Отправляю запрос к Data provide service для получения VerificationCode по id {}", id);
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<VerificationCodeDTO>> getVerificationCodeByUserId(Long userId) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint("user_id/"));
        endpoint.append(userId);

        log.debug(
                "Отправляю запрос к Data provide service для получения VerificationCode по user_id {}",
                userId
        );
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<VerificationCodeDTO>> getVerificationCodeByTelegramUserId(Long telegramUserId) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint("telegram_user_id/"));
        endpoint.append(telegramUserId);

        log.debug(
                "Отправляю запрос к Data provide service для получения VerificationCode по user_id {}",
                telegramUserId
        );
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.get()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<VerificationCodeDTO>> saveVerificationCode(ApiRequest<VerificationCodeDTO> request) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint("add/"));

        log.debug(
                "Отправляю запрос к Data provide service для сохранения VerificationCode {}",
                request
        );
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.post()
                .uri(endpoint.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<VerificationCodeDTO>> updateVerificationCode(ApiRequest<VerificationCodeDTO> request) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint("update/"));

        log.debug(
                "Отправляю запрос к Data provide service для обновления информации об VerificationCode {}",
                request
        );
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.post().uri(endpoint.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<Boolean>> deleteVerificationCode(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint("delete/"));
        endpoint.append(id);

        log.debug(
                "Отправляю запрос к Data provide service для удаления информации об VerificationCode с id {}",
                id
        );

        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.post().uri(endpoint.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
}
