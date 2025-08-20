package com.example.database.exception;


import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.ApiException;
import com.example.utils.text.ExceptionServiceUtils;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ApiResponse> userNotFoundExceptionHandle(UserNotFoundException e) {

        ApiResponse resp = getResponse(HttpStatus.BAD_REQUEST, e);

        log.debug("UserNotFoundException {}", resp);

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ApiResponse> noSuchElementExceptionHandle(NoSuchElementException e) {

        ApiResponse resp = getResponse(HttpStatus.BAD_REQUEST, e);

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ApiResponse> apiExceptionHandle(ApiException e) {

        ApiResponse resp = getResponse(HttpStatus.BAD_REQUEST, e);

        log.debug("ApiException {}" + resp);

        return ResponseEntity.status(resp.getStatus()).body(resp);

    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse> notSupportedExceptionHandler(Exception e) {

        ApiResponse resp = getResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Неизвестная ошибка - " + e.getMessage(), e);

        log.debug("Неизвестная ошибка {}", resp, e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    private String getExceptionStackTrace(Exception e) {
        return ExceptionServiceUtils.stackTraceToString(e);
    }

    private ApiResponse getResponse(HttpStatus status, Exception e) {

        return new ApiResponse(
                        status, e.getMessage(), null, getExceptionStackTrace(e)
                );
    }

    private ApiResponse getResponse(HttpStatus status, String msg, Exception e) {

        return new ApiResponse(
                status, msg, null, getExceptionStackTrace(e)
        );
    }

}
