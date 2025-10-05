package com.example.server.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    private static final String LOG_CONTROLLER_EXCEPTION_HANDLER_MSG = "Web server service exception handler: {}";

    @ExceptionHandler(Exception.class)
    protected Mono<ServerResponse> notSupportedExceptionHandler(Exception e) {

        String errorLogMsg = MessageFormat.format(LOG_CONTROLLER_EXCEPTION_HANDLER_MSG, e.getMessage());

        log.error(errorLogMsg, e);
        return Mono.just("Sorry, now but server is not working. Try later!")
                .flatMap(text -> ServerResponse.notFound().build());
    }

}
