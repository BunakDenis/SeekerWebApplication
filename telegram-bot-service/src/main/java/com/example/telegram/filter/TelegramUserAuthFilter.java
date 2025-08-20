package com.example.telegram.filter;


import com.example.telegram.bot.entity.User;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
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

        return DataBufferUtils.join(request.getBody())
                .flatMap(buffer -> {
                    String body = StandardCharsets.UTF_8.decode(buffer.toByteBuffer()).toString();
                    DataBufferUtils.release(buffer);

                    log.debug("Body запроса от Telegram API: {}", body);

                    Long telegramUserId = extractUserId(body);
                    if (telegramUserId == null) {
                        return chain.filter(exchange.mutate()
                                .request(decorateRequest(exchange, body, null))
                                .build());
                    }

                    return userService.getUserByTelegramUserId(telegramUserId)
                            .flatMap(resp -> {

                                if (Objects.nonNull(resp)) {
                                    User user = mapperService.toEntity(resp.getData(), User.class);

                                    log.debug("User {}", user);

                                    return userService.findByUsername(user.getUsername());
                                } else {
                                    return Mono.just(org.springframework.security.core.userdetails.User.builder().build());
                                }

                            })
                            .flatMap(userDetails -> {

                                if (Objects.nonNull(userDetails)) {
                                    Authentication auth = new UsernamePasswordAuthenticationToken(
                                            userDetails,                  // <-- principal = UserDetails
                                            null,
                                            userDetails.getAuthorities()
                                    );

                                    log.debug("Authentication = {}", auth);

                                    // sessionId в хедер
                                    String sessionId = String.valueOf(12345L);

                                    authService.authenticate(userDetails);

                                    log.debug("TelegramUserAuthFilter end.");

                                    return chain.filter(exchange.mutate()
                                                    .request(decorateRequest(exchange, body, sessionId))
                                                    .build())
                                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                                }

                                return chain.filter(exchange);

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
