package com.example.telegram.filter;


import com.example.data.models.entity.User;
import com.example.telegram.bot.service.AuthService;
import com.example.telegram.bot.service.ModelMapperService;
import com.example.telegram.bot.service.TelegramUserService;
import com.example.telegram.bot.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUserAuthFilter implements WebFilter {


    private final TelegramUserService telegramUserService;

    private final UserService userService;

    private final AuthService authService;

    private final ObjectMapper objectMapper;

    private final ModelMapperService mapperService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        log.debug("TelegramUserAuthFilter");

        ServerHttpRequest request = exchange.getRequest();

        String path = request.getURI().getPath();

        log.debug("Request URI path = {}", path);

        return DataBufferUtils.join(request.getBody())
                .flatMap(buffer -> {
                    String body = StandardCharsets.UTF_8.decode(buffer.toByteBuffer()).toString();
                    DataBufferUtils.release(buffer);

                    log.debug("Body запроса от Telegram API: {}", body);

                    // Пропускаем запросы с командами /auth и /register
                    if ("/api/bot/".equals(path) && (body.contains("/authorize") || body.contains("/register"))) {

                        log.debug("Обработка в фильтре запроса \"/authorize\" или \"/register\"");

                        return chain.filter(exchange.mutate()
                                .request(decorateRequest(exchange, body, ""))
                                .build()); // Пропускаем дальше по цепочке
                    }

                    Long telegramUserId = extractUserId(body);
                    if (telegramUserId == null) {
                        return chain.filter(exchange.mutate()
                                .request(decorateRequest(exchange, body, ""))
                                .build());
                    }

                    return userService.getUserByTelegramUserId(telegramUserId)
                            .flatMap(user -> {

                                if (Objects.nonNull(user)) {
                                    log.debug("User {}", user);
                                    return userService.findByUsername(user.getUsername());
                                } else {
                                    return Mono.just(org.springframework.security.core.userdetails.User.builder().build());
                                }

                            })
                            .flatMap(userDetails -> {

                                if (Objects.nonNull(userDetails)) {
                                    Authentication auth = new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );

                                    log.debug("Authentication = {}", auth);
                                    return Mono.just(auth);
                                } else {
                                    return Mono.empty();
                                }
                            })
                            .filter(Objects::nonNull)
                            .flatMap(auth -> {

                                // sessionId в хедер
                                String sessionId = String.valueOf(12345L);

                                SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

                                emptyContext.setAuthentication(auth);

                                log.debug("TelegramUserAuthFilter end.");

                                return chain.filter(exchange.mutate()
                                                .request(decorateRequest(exchange, body, sessionId))
                                                .build())
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

                            });
                });
    }

    private Long extractUserId(String body) {
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

    private ServerHttpRequest decorateRequest(ServerWebExchange exchange, String body, String sessionId) {
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
}
