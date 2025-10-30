package com.example.server.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Этот класс содержит логику обработки запросов для наших страниц.
 * Он не является контроллером, а просто компонентом,
 * методы которого будут вызываться маршрутизатором.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PageHandler {

    /**
     * Обрабатывает GET-запрос на главную страницу.
     * * @param request объект запроса (здесь он используется правильно)
     * @return Mono<ServerResponse> с указанием, какую страницу отрендерить.
     */
    public Mono<ServerResponse> getMainPage(ServerRequest request) {
        return ServerResponse.ok().render("pages/index");
    }

    public Mono<ServerResponse> registerPage(ServerRequest request) {
        return ServerResponse.ok().render("pages/telegramUserSignUp");
    }

    public Mono<ServerResponse> successRegPage(ServerRequest request) {
        String userId = request.pathVariable("user_id");

        log.debug("Отдаю страницу успешной регистрации для пользователя с id={}", userId);

        return ServerResponse.ok().render("pages/successReg", Map.of("user", "Денис Бунак"));
    }
}