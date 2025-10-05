package com.example.server.router;

import com.example.server.handler.PageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
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
    public RouterFunction<ServerResponse> mainPageRoute(PageHandler pageHandler) {
        return route()
                .GET("/", pageHandler::getMainPage)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> registerPageRoute(PageHandler handler) {
        return route()
                .GET("/register", handler::registerPage)
                .build();
    }
}