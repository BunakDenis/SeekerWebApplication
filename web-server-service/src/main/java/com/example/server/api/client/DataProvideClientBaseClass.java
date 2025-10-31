package com.example.server.api.client;

import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
abstract class DataProvideClientBaseClass {

    @Value("${data.provide.api.url}")
    protected String dataProviderURL;
    @Value("${data.provide.api.version}")
    protected String apiVersion;
    @Value("${api.key.header.name}")
    protected String apiKeyHeaderName;
    @Value("${web.server.api.key}")
    protected String apiKey;
    protected WebClient webClient;
    //protected final ModelMapperService mapperService;

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

    protected <K> Mono<ApiResponse<K>> sendPostRequest(
            String endpoint,
            ApiRequest<?> request,
            ParameterizedTypeReference<ApiResponse<K>> responseType) {

        return webClient.post()
                .uri(endpoint)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException(body)))
                )
                .bodyToMono(responseType);
    }

    protected Mono<ApiResponse<Boolean>> sendPostRequestWithBooleanResponse(String endpoint) {
        return webClient.post()
                .uri(endpoint)
                .accept(MediaType.APPLICATION_JSON)
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

    protected <K> Mono<ApiResponse<K>> sendGetRequest(
            String endpoint,
            ParameterizedTypeReference<ApiResponse<K>> responseType) {

        return webClient.get()
                .uri(endpoint)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service: {}", errorBody);
                                    // лучше вернуть ошибку, а не Mono.empty(), чтобы upstream получил сигнал об ошибке
                                    return Mono.error(new RuntimeException("Data provider error: " + errorBody));
                                }))
                .bodyToMono(responseType);
    }

    protected Mono<ApiResponse<Boolean>> sendGetRequestWithBooleanResponse(String endpoint) {
        return webClient.get()
                .uri(endpoint)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.debug("Получен ответ от Data provider service: {}", errorBody);
                                    // лучше вернуть ошибку, а не Mono.empty(), чтобы upstream получил сигнал об ошибке
                                    return Mono.error(new RuntimeException("Data provider error: " + errorBody));
                                }))
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

}
