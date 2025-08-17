package com.example.telegram.config;


import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.telegram.bot.service.TelegramUserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUserAuthFilter implements WebFilter {

    private final TelegramUserService userService;

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // читаем body (Flux<DataBuffer>)
        return DataBufferUtils.join(request.getBody())
                .flatMap(dataBuffer -> {
                    try {
                        String body = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer()).toString();
                        DataBufferUtils.release(dataBuffer);

                        log.debug("Body запроса от Telegram API: {}", body);

                        JsonNode json = objectMapper.readTree(body);

                        // Telegram update.message.from.id
                        Long telegramUserId = null;
                        if (json.has("message") && json.get("message").has("from")) {
                            telegramUserId = json.get("message").get("from").get("id").asLong();
                        } else if (json.has("callback_query")) {
                            telegramUserId = json.get("callback_query").get("from").get("id").asLong();
                        }

                        if (telegramUserId == null) {
                            log.debug("Не удалось извлечь telegramUserId из запроса");
                            return chain.filter(exchange);
                        }

                        return userService.findById(telegramUserId)
                                .flatMap(resp -> {
                                    TelegramSessionDTO dto = resp.getData();
                                    log.debug("Найден пользователь {}", dto);

                                    Authentication auth = new UsernamePasswordAuthenticationToken(
                                            dto,
                                            null,
                                            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                                    );

                                    return chain.filter(exchange)
                                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                                });
                    } catch (Exception e) {
                        log.error("Ошибка при парсинге body", e);
                        return chain.filter(exchange);
                    }
                });
    }
}
