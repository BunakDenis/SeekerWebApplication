package com.example.telegram.filter;


import com.example.data.models.consts.ResponseMessageProvider;
import com.example.data.models.consts.WarnMessageProvider;
import com.example.data.models.service.ApplicationFiltersService;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.telegram.bot.commands.Commands;
import com.example.telegram.bot.message.TelegramBotMessageSender;
import com.example.telegram.bot.service.*;
import com.example.telegram.bot.utils.update.UpdateUtilsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Mono;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramUserAuthFilter implements WebFilter {


    @Value("${telegram.bot.webhook-path}")
    private String webHookPath;
    @Value("${telegram.bot.api.path}")
    private String apiPath;
    private final AuthService authService;
    private final ApplicationFiltersService appFilterService;
    private final TelegramChatService telegramChatService;
    private final TelegramBotMessageSender sender;
    private final ObjectMapper objectMapper;
    private String requestBody;
    private Update update;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        log.debug("TelegramUserAuthFilter");

        ServerHttpRequest request = exchange.getRequest();

        String path = request.getURI().getPath();

        log.debug("Request URI path = {}", path);

        return DataBufferUtils.join(request.getBody())
                //Базовая проверка запроса
                .flatMap(buffer -> {

                    byte[] bytes;
                    StringBuffer body = new StringBuffer();

                    //Записываем buffer body в StringBuffer
                    try {
                        bytes = buffer.asInputStream().readAllBytes();
                        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

                        body.append(StandardCharsets.UTF_8.decode(byteBuffer));
                        DataBufferUtils.release(buffer);
                    } catch (IOException e) {
                        log.error("Request body is empty");
                    }

                    log.debug("Body от Telegram API: {}", body);

                    return Mono.just(body.toString());
                })
                .flatMap(body -> {

                    if (path.equals(webHookPath)) {

                        Update update;

                        try {
                            JsonNode jsonNode = objectMapper.readTree(body);
                            update = objectMapper.treeToValue(jsonNode, Update.class);
                        } catch (JsonProcessingException e) {

                            log.error("Тело не содержит Update");

                            return appFilterService.writeJsonErrorResponse(exchange.getResponse(),
                                    HttpStatus.BAD_REQUEST,
                                    ApiResponseUtilsService.fail(
                                            ResponseMessageProvider.REQUEST_BODY_DO_NOT_CONTAINS_TELEGRAM_UPDATE
                                    )
                            );

                        }

                        this.requestBody = body;
                        this.update = update;

                        Long telegramUserId = UpdateUtilsService.getTelegramUserId(update);

                        log.debug("Проверка запроса от Telegram API");
                        // Пропускаем запросы с командами /auth и /register
                        if (
                                body.contains(Commands.AUTHORIZE.getCommand()) ||
                                        body.contains(Commands.REGISTER.getCommand())
                        ) {

                            log.debug("Обработка в фильтре запроса \"/authorize\" или \"/register\"");

                            return chain.filter(exchange.mutate()
                                    .request(appFilterService.decorateRequestWithSessionId(exchange, body, ""))
                                    .build());
                        }

                        return telegramChatService.getTelegramChatByTelegramUserId(telegramUserId)
                                .flatMap(chat -> {

                                    log.debug("Проверка запроса от Telegram API");
                                    // Пропускаем запросы с командами /auth и /register
                                    if (chat.getUiElementValue().contains("/authorize") ||
                                            chat.getUiElementValue().contains("/register")
                                    ) {

                                        log.debug("Обработка в фильтре запроса \"/authorize\" или \"/register\"");

                                        return chain.filter(exchange.mutate()
                                                .request(appFilterService.decorateRequestWithSessionId(exchange, body, ""))
                                                .build());
                                    }

                                    return authService.authenticate(update)
                                            .then(chain.filter(exchange.mutate()
                                                            .request(
                                                                    appFilterService.decorateRequestWithSessionId(exchange, body, "")
                                                            )
                                                            .build()
                                                    )
                                            )
                                            // Ловим любые ошибки в цепочке
                                            .doOnError(ex -> log.error("Ошибка в TelegramUserAuthFilter - {}", ex.getMessage(), ex))
                                            .onErrorResume(ex -> {

                                                String modifiedBody = requestBody;

                                                log.debug("Класс ошибки {}", ex.getClass());

                                                //Если persistent просрочена отправляем юзеру сообщения об необходимости повторной авторизации
                                                //При прочих ошибках отправляем SorryMsg
                                                if (ex instanceof ExpiredJwtException) {
                                                    log.debug("Отправляю сообщение юзеру об необходимости повторной авторизации");
                                                    sender.sendMessage(
                                                            UpdateUtilsService.getChatId(this.update),
                                                            WarnMessageProvider.RE_AUTHORIZATION_MSG
                                                    );
                                                } else {

                                                    sender.sendMessage(
                                                            UpdateUtilsService.getChatId(this.update),
                                                            WarnMessageProvider.getSorryMsg(
                                                                    "бот временно недоступен, попробуйте написать позже!"
                                                            )
                                                    );

                                                    modifiedBody = requestBody.replaceFirst(
                                                            "\"update_id\"\\s*:\\s*\\d+",
                                                            "\"update_id\":0"
                                                    );

                                                }

                                                return chain.filter(exchange.mutate()
                                                        .request(appFilterService.decorateRequestWithSessionId(exchange, modifiedBody, ""))
                                                        .build());
                                            });
                                });

                    }

                    if (path.equals(apiPath)) {

                        log.debug("Проверка запроса к API \"{}\"", path);

                        return chain.filter(exchange.mutate()
                                .request(appFilterService.decorateRequestWithSessionId(exchange, body, ""))
                                .build());
                    }

                    //Если запрос получен на не существующие endpoints отправляем request с HttpStatus 404
                    return appFilterService.writeJsonErrorResponse(exchange.getResponse(),
                            HttpStatus.NOT_FOUND,
                            ApiResponseUtilsService.fail(
                                    ResponseMessageProvider.getEndpointNotFoundMsg(path)
                            )
                    );
                })
                .switchIfEmpty(
                        appFilterService.writeJsonErrorResponse(
                                exchange.getResponse(),
                                HttpStatus.BAD_REQUEST,
                                ApiResponseUtilsService.fail(
                                        ResponseMessageProvider.REQUEST_BODY_DO_NOT_CONTAINS_TELEGRAM_UPDATE
                                )
                        )
                )
                .doOnError(err -> log.error("Ошибка в фильтре аутентификации - {}", err.getMessage(), err))
                .onErrorResume(err -> {

                    if (err instanceof WebClientRequestException) log.debug("WebClientRequestException");
                    sender.sendMessage(
                            UpdateUtilsService.getChatId(update),
                            WarnMessageProvider.getSorryMsg("бот временно недоступен, попробуйте написать позже!")
                    );

                    return appFilterService.writeJsonErrorResponse(
                            exchange.getResponse(),
                            HttpStatus.OK,
                            ApiResponseUtilsService.success("Извините, произошла ошибка. Повторите запрос позже.")
                    );
                });

    }
}
