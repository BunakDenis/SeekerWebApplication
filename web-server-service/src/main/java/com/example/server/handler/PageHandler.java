package com.example.server.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Этот класс содержит логику обработки запросов для наших страниц.
 * Он не является контроллером, а просто компонентом,
 * методы которого будут вызываться маршрутизатором.
 */
@Component
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
        return ServerResponse.ok().render("pages/userSignUp");
    }
}