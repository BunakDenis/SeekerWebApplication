package com.example.server.router;

import com.example.server.handler.PageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Класс конфигурации, который определяет все маршруты нашего приложения
 * в функциональном стиле.
 */
@Configuration
public class PageRouter {

    /**
     * Создает бин RouterFunction, который Spring будет использовать для маршрутизации.
     * * @param pageHandler наш обработчик, который Spring внедрит автоматически.
     * @return сконфигурированный роутер.
     */
    @Bean
    public RouterFunction<ServerResponse> mainAndAdditionalPagesRoute(PageHandler pageHandler) {
        return route()
                .path("", builder -> builder
                        .GET("/", pageHandler::getMainPage)
                        .GET("/tg_register", pageHandler::registerPage)
                        .GET("/successReg/{user_id}", pageHandler::successRegPage)
                )
                .build();
    }
}