package com.example.telegram.api.clients;

import com.example.telegram.dto.responce.ActuatorHealthResponse;
import com.example.telegram.dto.responce.FileServiceResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class UsefulToolsClient {

    @Value("${api.useful.tools.url}")
    private String baseUrlWithoutPort;

    //@Value("${API_USEFUL_TOOLS_PORT}")
    private String port = "8080";

    @Value("${api.useful.tools.file.service.endpoint}")
    private String usefulToolsFileServiceEndpoint;

    private WebClient webClient;

    @PostConstruct
    public void init() {

        String baseURL = baseUrlWithoutPort + ":" + port;

        log.debug("Устанавливаем baseUrl = " + baseURL);

        this.webClient = WebClient.builder()
                .baseUrl(baseURL)
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    log.debug("Request URL: {}", clientRequest.url());
                    return Mono.just(clientRequest);
                }))
                .build();
    }

    public Mono<ActuatorHealthResponse> getUsefulToolsHeals() {
        // Определяем путь к эндпоинту
        String endpointPath = "/actuator/health";

        try {
            return webClient.get()
                    // Строим URI с query параметрами
                    .uri(uriBuilder -> uriBuilder.path(endpointPath)
                            .build())
                    // Указываем, что ожидаем JSON в ответ
                    .accept(MediaType.APPLICATION_JSON)
                    // Получаем ответ
                    .retrieve()
                    // Обработка ошибок HTTP статусов (опционально, но рекомендуется)
                    // Например, если получили 4xx или 5xx
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                    .flatMap(errorBody -> Mono.error(new RuntimeException(
                                            String.format("Error from MainUtilsService: Status %d, Body: %s",
                                                    response.statusCode().value(), errorBody)))))
                    // Преобразуем тело ответа в наш DTO
                    .bodyToMono(ActuatorHealthResponse.class)
                    .log();
        } catch (Exception e) {
            log.debug("Негативный ответ от useful tools service\n" + e.getMessage(), e);
        }

        return null;
    }

    public Mono<FileServiceResponse> decodeAudio() {
        // Определяем путь к эндпоинту
        String endpointPath = usefulToolsFileServiceEndpoint + "decode";

        // Выполняем POST запрос
        Mono<FileServiceResponse> fileServiceResponse = webClient.post()
                // Строим URI с query параметрами
                .uri(uriBuilder -> uriBuilder.path(endpointPath)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                // Указываем, что ожидаем JSON в ответ
                .accept(MediaType.APPLICATION_JSON)
                // Получаем ответ
                .retrieve()
                // Обработка ошибок HTTP статусов (опционально, но рекомендуется)
                // Например, если получили 4xx или 5xx
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class) // Можно прочитать тело ошибки
                                .flatMap(errorBody -> Mono.error(new RuntimeException(
                                        String.format("Error from MainUtilsService: Status %d, Body: %s",
                                                response.statusCode().value(), errorBody)))))
                // Преобразуем тело ответа в наш DTO
                .bodyToMono(FileServiceResponse.class)
                .doOnNext(response -> log.debug("Получен ответ от useful-tools: " + response.toString()));

        return fileServiceResponse;
    }

}