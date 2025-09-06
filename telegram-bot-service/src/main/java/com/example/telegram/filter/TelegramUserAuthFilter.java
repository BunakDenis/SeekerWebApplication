package com.example.telegram.filter;


import com.example.data.models.consts.RequestMessageProvider;
import com.example.data.models.consts.WarnMessageProvider;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.service.AuthService;
import com.example.telegram.bot.service.ModelMapperService;
import com.example.telegram.bot.service.TelegramUserService;
import com.example.telegram.bot.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUserAuthFilter implements WebFilter {


    private final TelegramUserService telegramUserService;
    private final UserService userService;
    private final AuthService authService;
    private final ApplicationFiltersService appFilterService;
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

                        byte[] bytes;
                        StringBuffer body = new StringBuffer();

                        try {
                            bytes = buffer.asInputStream().readAllBytes();
                            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

                            body.append(StandardCharsets.UTF_8.decode(byteBuffer));
                            DataBufferUtils.release(buffer);
                        } catch (IOException e) {
                            log.error("Request body is empty");
                        }

                        log.debug("Body от Telegram API: {}", body);

                        // Пропускаем запросы с командами /auth и /register
                        if (
                                "/api/bot/".equals(path) &&
                                        (body.indexOf("/authorize") != -1 || body.indexOf("/register") != -1)) {

                            log.debug("Обработка в фильтре запроса \"/authorize\" или \"/register\"");

                            return chain.filter(exchange.mutate()
                                    .request(appFilterService.decorateRequest(exchange, body.toString(), ""))
                                    .build());
                        }

                        Long telegramUserId = appFilterService.extractTelegramUserId(body.toString());
                        if (Objects.isNull(telegramUserId)) {
                            return appFilterService.writeJsonErrorResponse(exchange.getResponse(),
                                    HttpStatus.UNAUTHORIZED,
                                    ApiResponseUtilsService.fail(RequestMessageProvider.REQUEST_BODY_IS_EMPTY));
                        }

                        Long chatId = appFilterService.extractChatId(body.toString());
                        this.requestBody = body.toString();
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
                                                    .request(appFilterService.decorateRequest(exchange, body.toString(), sessionId))
                                                    .build())
                                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

                                });
                    })
                    .switchIfEmpty(
                            appFilterService.writeJsonErrorResponse(exchange.getResponse(),
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
                            .request(appFilterService.decorateRequest(exchange, modifiedBody, ""))
                            .build());
                });
    }
}
