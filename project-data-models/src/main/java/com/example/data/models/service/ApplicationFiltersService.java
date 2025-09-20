package com.example.data.models.service;

import com.example.data.models.entity.dto.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationFiltersService {

    private final ObjectMapper objectMapper;


    public Mono<StringBuffer> getBodyFromRequest(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .defaultIfEmpty(exchange.getResponse().bufferFactory().wrap(new byte[0]))
                .flatMap(buffer -> {
                    byte[] bytes;
                    StringBuffer body = new StringBuffer();
                    try {
                        bytes = buffer.asInputStream().readAllBytes();
                        if (bytes.length > 0) {
                            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                            body.append(StandardCharsets.UTF_8.decode(byteBuffer));
                        }
                    } catch (IOException e) {
                        log.error("Error reading request body", e);
                    } finally {
                        DataBufferUtils.release(buffer);
                    }
                    return Mono.just(body);
                });

    }
    public ServerHttpRequest decorateRequestWithSessionId(ServerWebExchange exchange, String body, String sessionId) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                return Flux.defer(() -> {
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    return Mono.just(buffer);
                });
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.putAll(super.getHeaders());
                if (sessionId != null) {
                    headers.add("X-Session-Id", sessionId);
                }
                headers.setContentLength(bytes.length);
                return headers;
            }
        };
    }
    public ServerHttpRequest decorateRequestWithApiKey(ServerWebExchange exchange, String body, String apiKey) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                return Flux.defer(() -> {
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    return Mono.just(buffer);
                });
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.putAll(super.getHeaders());
                if (apiKey != null) {
                    headers.add("X-Session-Id", apiKey);
                }
                headers.setContentLength(bytes.length);
                return headers;
            }
        };
    }
    public Mono<Void> writeJsonErrorResponse(ServerHttpResponse response,
                                              HttpStatus status,
                                              ApiResponse body) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }

    public Long extractTelegramUserId(String body) {
        try {
            JsonNode json = objectMapper.readTree(body);
            if (json.has("message") && json.get("message").has("from")) {
                return json.get("message").get("from").get("id").asLong();
            } else if (json.has("callback_query")) {
                return json.get("callback_query").get("from").get("id").asLong();
            }
        } catch (Exception e) {
            log.error("Ошибка парсинга JSON: {}", e.getMessage());
        }
        return null;
    }

    public Long extractChatId(String body) {
        try {
            JsonNode json = objectMapper.readTree(body);
            if (json.has("message") && json.get("message").has("chat")) {
                return json.get("message").get("chat").get("id").asLong();
            } else if (json.has("callback_query")) {
                return json.get("callback_query").get("chat").get("id").asLong();
            }
        } catch (Exception e) {
            log.error("Ошибка парсинга JSON: {}", e.getMessage());
        }
        return null;
    }
}
