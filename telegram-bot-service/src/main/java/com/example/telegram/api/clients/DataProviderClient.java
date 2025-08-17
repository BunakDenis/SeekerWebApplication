package com.example.telegram.api.clients;


import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.telegram.TelegramChatDTO;
import com.example.data.models.entity.dto.request.ApiRequest;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.exception.ApiException;
import com.example.telegram.bot.entity.TelegramChat;
import com.example.telegram.bot.entity.User;
import com.example.telegram.bot.service.ModelMapperService;
import com.example.telegram.bot.service.TelegramChatService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Log4j2
public class DataProviderClient {

    @Value("${data.provide.api.url}")
    private String dataProviderURL;

    @Value("${data.provide.api.version}")
    private String apiVersion;

    private String apiChatEndpoint;

    private String apiSessionEndPoint;

    private WebClient webClient;

    private final ModelMapperService mapperService;

    @PostConstruct
    public void init() {
        apiChatEndpoint = "/chat";

        apiSessionEndPoint = "/session";

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

    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserId(Long id) {

        String endpoint = "/user/get/telegram_user_id/" + id;

        try {
            return webClient.get()
                    .uri(endpoint)
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

    public Mono<ApiResponse> checkTelegramUserAuthentication(Long telegramUserId) {

        String endpoint = "/user/check/auth/" + telegramUserId;

        try {
            return webClient.get()
                    .uri(builder -> builder.path(endpoint).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                    .flatMap(errorBody -> {
                                        log.debug("Получен ответ от Data provider service {}", errorBody);
                                        return Mono.empty();
                                    }))
                    .bodyToMono(ApiResponse.class);
        } catch (Exception e) {
            log.debug("Ошибка отправки сообщения проверки аутентификации юзера" + e.getMessage());
        }
        return null;
    }

    public Mono<ApiResponse<TelegramUserDTO>> getTelegramUserById(Long id) {

        String endpoint = "/telegram/user/get/" + id;

        try {
            return webClient.get()
                    .uri(endpoint)
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

        TelegramChatDTO dto = mapperService.telegramChatToDTO(chat);

        log.debug("Отправляю запрос к Data provide service для записи {}", dto);
        log.debug("Отправка на endpoint " + apiChatEndpoint + "/add/");

        try {
            Mono<ApiResponse<TelegramChatDTO>> result = webClient.post().uri(apiChatEndpoint + "/add/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(ApiRequest.<TelegramChatDTO>builder()
                            .data(dto)
                            .build())
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

            result.subscribe(resp -> log.debug(resp));

            return result;

        } catch (Exception e) {
            log.debug("Ошибка отправки запроса " + apiChatEndpoint + "/add/ " + chat);
            log.debug("Текст ошибки {}", e.getMessage(), e);
            return null;
        }
    }

    public Mono<ApiResponse<TelegramChatDTO>> getTelegramChats(Long id) {

        log.debug("Отправляю запрос к Data provide service для получения чата {}", id);
        log.debug("Отправка на endpoint " + apiChatEndpoint + "/get/" + id);

        try {
            Mono<ApiResponse<TelegramChatDTO>> result = webClient.get().uri(apiChatEndpoint + "/get/" + id)
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
            log.debug("Ошибка отправки запроса " + apiChatEndpoint + "/get/" + id);
            log.debug("Текст ошибки {}", e.getMessage(), e);
            return null;
        }
    }

    public Mono<ApiResponse<TelegramSessionDTO>> getTelegramSessionByTelegramUserId(Long id) {

        try {
            return webClient.get()
                    .uri(apiSessionEndPoint + "/get/telegramUserId/" + id)
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

}
