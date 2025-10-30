package com.example.server.api.client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
abstract class DataProvideClientBaseClass {

    @Value("${data.provide.api.url}")
    private String dataProviderURL;
    @Value("${data.provide.api.version}")
    private String apiVersion;
    @Value("${api.key.header.name}")
    private String apiKeyHeaderName;
    @Value("${telegram.api.key}")
    private String apiKey;
    private WebClient webClient;
    //private final ModelMapperService mapperService;

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

}
