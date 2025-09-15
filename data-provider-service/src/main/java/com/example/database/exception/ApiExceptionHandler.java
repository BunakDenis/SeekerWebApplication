package com.example.database.exception;


import com.example.data.models.entity.User;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.ApiException;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.utils.text.ExceptionServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

import static com.example.data.models.consts.RequestMessageProvider.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiResponse> entityNotFoundExceptionHandle(EntityNotFoundException e) {

        ApiResponse resp = ApiResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(getEntityNotFoundMessage(e.getEntityClassName()))
                .debugMsg(getExceptionStackTrace(e))
                .build();

        return ResponseEntity.status(resp.getStatus()).body(resp);

    }
    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ApiResponse> userNotFoundExceptionHandle(UserNotFoundException e) {

        ApiResponse resp = ApiResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(getEntityNotFoundMessage(new User()))
                .debugMsg(getExceptionStackTrace(e))
                .build();

        log.debug("UserNotFoundException {}", resp);

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }
    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ApiResponse> noSuchElementExceptionHandle(NoSuchElementException e) {

        ApiResponse resp = getResponse(HttpStatus.NOT_FOUND, e);

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

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }
    private ApiResponse getResponse(HttpStatus status, Exception e) {

        return ApiResponse.builder()
                .status(status)
                .message(e.getMessage())
                .debugMsg(getExceptionStackTrace(e))
                .build();

    }
    private ApiResponse getResponse(HttpStatus status, String msg, Exception e) {

        return ApiResponse
                .builder()
                .status(status)
                .message(msg)
                .debugMsg(getExceptionStackTrace(e))
                .build();
    }
    private String getExceptionStackTrace(Exception e) {
        return ExceptionServiceUtils.stackTraceToString(e);
    }

}
