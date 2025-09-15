package com.example.telegram.api.clients;


import com.example.data.models.entity.TelegramUser;
import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.dto.response.CheckUserResponse;
import com.example.data.models.entity.dto.telegram.*;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.request.ApiRequest;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.ApiException;
import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.service.ModelMapperService;
import io.swagger.annotations.Api;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import static com.example.telegram.api.clients.DataProviderEndpointsConsts.*;

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
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
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
    public Mono<ApiResponse<TelegramUserDTO>> createTelegramUser(TelegramUserDTO dto) {

        StringBuilder endpoint = new StringBuilder(getApiTelegramUserEndpoint("add/"));

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
    public Mono<ApiResponse<TelegramUserDTO>> getTelegramUserById(Long id) {

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
    public Mono<ApiResponse<Boolean>> deleteTelegramUser(Long id) {

        return webClient.post()
                .uri(getApiTelegramUserEndpoint("delete/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
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
                                            String.format("Error from MainUtilsService: Status %d, Body: %s",
                                                    response.statusCode().value(), errorBody)));
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TelegramChatDTO>> getTelegramChatWithTelegramUser(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiChatEndpoint("telegram_user/"));
        endpoint.append(id);

        log.debug("Отправляю запрос к Data provide service для получения чата с телеграм юзером {}", id);
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.get()
                .uri(endpoint.toString()).accept(MediaType.APPLICATION_JSON)
                .retrieve()
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
    public Mono<ApiResponse<TelegramSessionDTO>> createTelegramSession(TelegramSessionDTO dto) {

        StringBuilder endpoint = new StringBuilder(getApiSessionEndpoint("add/"));

        log.debug("Отправляю запрос к Data provide service на сохранение TelegramSession");
        log.debug("Отправка на endpoint {}", endpoint);

        return webClient.post()
                .uri(endpoint.toString())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
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
    public Mono<ApiResponse<TransientSessionDTO>> saveTransientSession(TransientSessionDTO dto) {

        ApiRequest<TransientSessionDTO> response = new ApiRequest<>(dto);

        return webClient.post()
                .uri(getApiTransientSessionEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(response)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<TransientSessionDTO>> updateTransientSession(TransientSessionDTO dto) {

        ApiRequest<TransientSessionDTO> response = new ApiRequest<>(dto);

        return webClient.post()
                .uri(getApiTransientSessionEndpoint("update/"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(response)
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
    public Mono<ApiResponse<PersistentSessionDTO>> savePersistentSession(PersistentSessionDTO dto) {

        ApiRequest<PersistentSessionDTO> response = new ApiRequest<>(dto);

        return webClient.post()
                .uri(getApiPersistentSessionEndpoint("add/"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(response)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }
    public Mono<ApiResponse<PersistentSessionDTO>> updatePersistentSession(PersistentSessionDTO dto) {

        ApiRequest<PersistentSessionDTO> response = new ApiRequest<>(dto);

        return webClient.post()
                .uri(getApiPersistentSessionEndpoint("update/"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(response)
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
