package com.example.database.exception;


import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.ApiException;
import com.example.database.entity.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Log4j2
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ApiResponse> userNotFoundExceptionHandle(UserNotFoundException e) {
        ApiResponse resp = new ApiResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);

        log.debug("UserNotFoundException {}", resp);

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ApiResponse> apiExceptionHandle(ApiException e) {
        ApiResponse resp = new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);

        log.debug("ApiException {}" + resp);

        return ResponseEntity.status(resp.getStatus()).body(resp);

    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse> notSupportedExceptionHandler(Exception e) {

        ApiResponse resp = new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Неизвестная ошибка - " + e.getMessage());

        log.debug("Неизвестная ошибка {}", resp, e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }



}
