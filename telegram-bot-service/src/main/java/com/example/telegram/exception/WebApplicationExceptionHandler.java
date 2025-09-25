package com.example.telegram.exception;

import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.utils.ApiResponseUtilsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebApplicationExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        log.debug("WebApplicationExceptionHandler, exchanger = {}", exchange);

        return null;
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse> nullPointerExceptionHandle(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseUtilsService.fail(ex.getMessage())
        );
    }
}
