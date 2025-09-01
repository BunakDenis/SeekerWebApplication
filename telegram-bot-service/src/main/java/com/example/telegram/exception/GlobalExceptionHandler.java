package com.example.telegram.exception;


import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.ApiException;
import com.example.utils.text.ExceptionServiceUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {


    @ExceptionHandler(ApiException.class)
    protected ApiResponse apiExceptionHandler(Exception e) {
        return ApiResponse.builder()
                .message(e.getMessage())
                .debugMsg(ExceptionServiceUtils.stackTraceToString(e))
                .build();
    }

    @ExceptionHandler(Exception.class)
    protected ApiResponse notSupportedExceptionHandler(Exception e) {

        log.error("Неизвестная ошибка {}", e);

        return ApiResponse.builder().message(e.getMessage()).build();
    }

}
