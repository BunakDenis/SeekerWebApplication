package com.example.telegram.api.clients;


import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.dto.response.CheckUserResponse;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.data.models.entity.dto.request.ApiRequest;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.exception.ApiException;
import com.example.data.models.entity.TelegramChat;
import com.example.telegram.bot.service.ModelMapperService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mockserver.serialization.model.VerificationDTO;
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
@Log4j2
public class DataProviderClient {

    @Value("${data.provide.api.url}")
    private String dataProviderURL;

    @Value("${data.provide.api.version}")
    private String apiVersion;

    private WebClient webClient;

    private final ModelMapperService mapperService;

    @PostConstruct
    public void init() {

        String baseURL = dataProviderURL + apiVersion;

        log.debug("Устанавливаем baseUrl для Data provider client = " + baseURL);

        this.webClient = WebClient.builder()
                .baseUrl(baseURL)
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    log.debug("Request URL: {}", clientRequest.url());
                    return Mono.just(clientRequest);
                }))
                .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    log.debug("Response status code: {}", clientResponse.statusCode());
                    return Mono.just(clientResponse);
                }))
                .build();
    }

    public Mono<ApiResponse<UserDTO>> getUserById(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("/get/id/"));
        endpoint.append(id);

        try {
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
        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения получения User {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<UserDTO>> getUserByUsername(String username) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("/get/username/"));
        endpoint.append(username);

        try {
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
        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения получения User {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<UserDTO>> getUserByEmail(String email) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("/get/email/"));
        endpoint.append(email);

        try {
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
        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения получения User {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserId(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("/get/telegram_user_id/"));
        endpoint.append(id);

        try {
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
        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения получения TelegramUser {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserIdWithUserDetails(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("/user_details/get/telegram_user_id/"));
        endpoint.append(id);

        try {
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
        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения получения TelegramUser {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserIdWithTelegramUser(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("/telegram_user/get/telegram_user_id/"));
        endpoint.append(id);

        try {
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
        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения получения TelegramUser {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserIdFull(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("/full/get/telegram_user_id/"));
        endpoint.append(id);

        try {
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
        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения получения TelegramUser {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<CheckUserResponse>> checkTelegramUserAuthentication(Long telegramUserId) {

        StringBuilder endpoint = new StringBuilder(getApiUserEndpoint("/check/auth/"));
        endpoint.append(telegramUserId);

        try {
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
        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения проверки аутентификации юзера" + e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<TelegramUserDTO>> getTelegramUserById(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiTelegramUserEndpoint("/get/"));
        endpoint.append(id);

        try {
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
        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения получения TelegramUser {}", e.getMessage());
        }
        return null;

    }
    public Mono<ApiResponse<TelegramChatDTO>> saveTelegramChat(TelegramChat chat) {

        StringBuilder endpoint = new StringBuilder(getApiChatEndpoint("/add/"));

        TelegramChatDTO dto = mapperService.toDTO(chat, TelegramChatDTO.class);
        TelegramUserDTO telegramUserDTO = mapperService.toDTO(chat.getTelegramUser(), TelegramUserDTO.class);

        ApiRequest<TelegramChatDTO> request = new ApiRequest(dto);

        request.addIncludeObject("telegram_user", telegramUserDTO);

        log.debug("Отправляю запрос к Data provide service для записи чата {}", dto);
        log.debug("Отправка на endpoint {}", endpoint);

        try {
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
        } catch (Exception e) {
            log.debug("Ошибка отправки запроса {}", endpoint);
            log.debug("Текст ошибки {}", e.getMessage(), e);
            return null;
        }
    }
    public Mono<ApiResponse<TelegramChatDTO>> getTelegramChat(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiChatEndpoint("/get/"));
        endpoint.append(id);

        log.debug("Отправляю запрос к Data provide service для получения чата {}", id);
        log.debug("Отправка на endpoint {}", endpoint);

        try {
            Mono<ApiResponse<TelegramChatDTO>> result = webClient.get().uri(endpoint.toString())
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

            result.subscribe(resp -> log.debug(resp));

            return result;

        } catch (Exception e) {
            log.debug("Ошибка отправки запроса {}", endpoint);
            log.debug("Текст ошибки {}", e.getMessage(), e);
            return null;
        }
    }
    public Mono<ApiResponse<TelegramChatDTO>> getTelegramChatWithTelegramUser(Long id) {

        StringBuilder endpoint = new StringBuilder( getApiChatEndpoint("/telegram_user/get/"));
        endpoint.append(id);

        log.debug("Отправляю запрос к Data provide service для получения чата с телеграм юзером {}", id);
        log.debug("Отправка на endpoint {}", endpoint);

        try {

            return webClient.get()
                    .uri(endpoint.toString()).accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<>() {
                    });

        } catch (Exception e) {
            log.debug("Ошибка отправки запроса {}", endpoint);
            log.debug("Текст ошибки {}", e.getMessage(), e);
            return null;
        }
    }
    public Mono<ApiResponse<TelegramSessionDTO>> getTelegramSessionByTelegramUserId(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiSessionEndpoint("/get/telegramUserId/"));
        endpoint.append(id);

        log.debug("Отправляю запрос к Data provide service для получения TelegramSession по telegram_user_id {}", id);
        log.debug("Отправка на endpoint {}", endpoint);

        try {
            return webClient.get()
                    .uri(endpoint.toString())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<>() {
                    });

        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения получения TelegramSession по telegram_user_id {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<VerificationCodeDTO>> getVerificationCodeById(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint("/get/" + id));
        endpoint.append(id);

        log.debug("Отправляю запрос к Data provide service для получения VerificationCode по id {}", id);
        log.debug("Отправка на endpoint {}", endpoint);

        try {
            return webClient.get()
                    .uri(endpoint.toString())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<>() {
                    });

        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения получения VerificationCode по id {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<VerificationCodeDTO>> getVerificationCodeByUserId(Long userId) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint("/get/user_id/"));
        endpoint.append(userId);

        log.debug(
                "Отправляю запрос к Data provide service для получения VerificationCode по user_id {}",
                userId
        );
        log.debug("Отправка на endpoint {}", endpoint);

        try {
            return webClient.get()
                    .uri(endpoint.toString())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<>() {
                    });

        } catch (Exception e) {
            log.error("Ошибка отправки сообщения получения VerificationCode по user_id {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<VerificationCodeDTO>> getVerificationCodeByTelegramUserId(Long telegramUserId) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint("/get/telegram_user_id/"));
        endpoint.append(telegramUserId);

        log.debug(
                "Отправляю запрос к Data provide service для получения VerificationCode по user_id {}",
                telegramUserId
        );
        log.debug("Отправка на endpoint {}", endpoint);

        try {
            return webClient.get()
                    .uri(endpoint.toString())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<>() {
                    });

        } catch (Exception e) {
            log.error("Ошибка отправки сообщения получения VerificationCode по user_id {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<VerificationCodeDTO>> saveVerificationCode(ApiRequest<VerificationCodeDTO> request) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint("/add/"));

        log.debug(
                "Отправляю запрос к Data provide service для сохранения VerificationCode {}",
                request
        );
        log.debug("Отправка на endpoint {}", endpoint);

        try {
            return webClient.post()
                    .uri(endpoint.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<>() {
                    });

        } catch (Exception e) {
            log.error("Ошибка отправки сообщения сохранения VerificationCode {}", e.getMessage());
        }
        return null;

    }
    public Mono<ApiResponse<VerificationCodeDTO>> updateVerificationCode(ApiRequest<VerificationCodeDTO> request) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint("/update/"));

        log.debug(
                "Отправляю запрос к Data provide service для обновления информации об VerificationCode {}",
                request
        );
        log.debug("Отправка на endpoint {}", endpoint);

        try {

            return webClient.post().uri(endpoint.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<>() {
                    });

        } catch (Exception e) {
            log.error("Ошибка отправки сообщения для обновления информации об VerificationCode {}", e.getMessage());
        }
        return null;
    }
    public Mono<ApiResponse<Boolean>> deleteVerificationCode(Long id) {

        StringBuilder endpoint = new StringBuilder(getApiOtpCodeEndpoint("/delete/"));
        endpoint.append(id);

        log.debug(
                "Отправляю запрос к Data provide service для удаления информации об VerificationCode с id {}",
                id
        );

        log.debug("Отправка на endpoint {}", endpoint);

        try {

            return webClient.post().uri(endpoint.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<>() {
                    });

        } catch (Exception e) {
            log.error(
                    "Ошибка отправки сообщения для удаления информации об VerificationCode {}",
                    e.getMessage()
            );
        }
        return null;
    }

}
