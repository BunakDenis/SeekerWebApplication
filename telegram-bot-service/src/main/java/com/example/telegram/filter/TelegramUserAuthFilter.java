package com.example.telegram.filter;


import com.example.data.models.consts.RequestMessageProvider;
import com.example.data.models.consts.WarnMessageProvider;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.service.AuthService;
import com.example.telegram.bot.service.ModelMapperService;
import com.example.telegram.bot.service.TelegramUserService;
import com.example.telegram.bot.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.telegram.telegrambots.meta.api.objects.Update;
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
    private final TelegramBotMessageSender sender;
    private final ObjectMapper objectMapper;
    private final ModelMapperService mapperService;
    private String requestBody;
    private Long chatId;

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

                        log.debug("Body от Telegram API: {}", body);

                        // Пропускаем запросы с командами /auth и /register
                        if ("/api/bot/".equals(path) && (body.contains("/authorize") || body.contains("/register"))) {

                            log.debug("Обработка в фильтре запроса \"/authorize\" или \"/register\"");

                            return chain.filter(exchange.mutate()
                                    .request(decorateRequest(exchange, body, ""))
                                    .build()); // Пропускаем дальше по цепочке
                        }

                        Long telegramUserId = extractTelegramUserId(body);
                        if (Objects.isNull(telegramUserId)) {
                            return writeJsonErrorResponse(exchange.getResponse(),
                                    HttpStatus.UNAUTHORIZED,
                                    ApiResponseUtilsService.fail(RequestMessageProvider.REQUEST_BODY_IS_EMPTY));
                        }

                        Long chatId = extractChatId(body);
                        this.requestBody = body;
                        this.chatId = chatId;

                        return userService.getUserByTelegramUserId(telegramUserId)
                                .flatMap(user -> {
                                    log.debug("User {}", user);
                                    return userService.findByUsername(user.getUsername());
                                })
                                .flatMap(userDetails -> {
                                    Authentication auth = new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );

                                    log.debug("Authentication = {}", auth);
                                    return Mono.just(auth);
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
                    })
                .switchIfEmpty(
                        writeJsonErrorResponse(exchange.getResponse(),
                                HttpStatus.BAD_REQUEST,
                                ApiResponseUtilsService.fail(RequestMessageProvider.REQUEST_BODY_IS_EMPTY))
                )
                // Ловим любые ошибки в цепочке
                .doOnError(ex -> log.error("Ошибка обработки update: {}", ex.getMessage(), ex))
                .onErrorResume(ex -> {
                    sender.sendMessage(
                            chatId,
                            WarnMessageProvider.getSorryMsg(
                                    "бот временно недоступен, попробуйте написать позже!"
                            )
                    );

                    String modifiedBody = requestBody.replaceFirst(
                            "\"update_id\"\\s*:\\s*\\d+",
                            "\"update_id\":0"
                    );

                    return chain.filter(exchange.mutate()
                            .request(decorateRequest(exchange, modifiedBody, ""))
                            .build());
                });
    }

    private Long extractTelegramUserId(String body) {
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

    private Long extractChatId(String body) {
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

    private Mono<Void> writeJsonErrorResponse(ServerHttpResponse response,
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
}
