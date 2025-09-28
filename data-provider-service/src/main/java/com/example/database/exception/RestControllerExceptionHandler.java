package com.example.database.exception;


import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.exception.*;
import com.example.utils.text.ExceptionServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.resource.NoResourceFoundException;


import java.util.NoSuchElementException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestControllerExceptionHandler {


    private static final String LOG_EXCEPTION_HANDLE_MSG = "RestControllerExceptionHandler handle exception {}";


    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ApiResponse<Object>> endPointNotFoundExceptionHandler(NoResourceFoundException e) {

        log.error(LOG_EXCEPTION_HANDLE_MSG, e.getClass().getCanonicalName(), e.getMessage(), e);

        ApiResponse response = ApiResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getReason())
                .debugMsg(getExceptionStackTrace(e))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(EntityNullException.class)
    protected ResponseEntity<ApiResponse<Object>> entityNullExceptionHandler(EntityNullException e) {

        log.error(LOG_EXCEPTION_HANDLE_MSG, e.getClass().getCanonicalName(), e.getMessage(), e);

        ApiResponse<Object> response = getResponse(HttpStatus.BAD_REQUEST, e);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiResponse<Object>> entityNotFoundExceptionHandle(EntityNotFoundException e) {

        log.error(LOG_EXCEPTION_HANDLE_MSG, e.getClass().getCanonicalName(), e.getMessage(), e);

        ApiResponse<Object> resp = getResponse(HttpStatus.NOT_FOUND, e);

        return ResponseEntity.status(resp.getStatus()).body(resp);

    }

    @ExceptionHandler(EntityNotSavedException.class)
    protected ResponseEntity<ApiResponse<Object>> entityNotSavedExceptionHandler(EntityNotSavedException e) {

        log.error(LOG_EXCEPTION_HANDLE_MSG, e.getClass().getCanonicalName(), e.getMessage(), e);

        ApiResponse<Object> response = getResponse(HttpStatus.BAD_REQUEST, e);

        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @ExceptionHandler(EntityNullFieldException.class)
    protected ResponseEntity<ApiResponse<Object>> entityNullFieldExceptionHandler(EntityNullFieldException e) {

        log.error(LOG_EXCEPTION_HANDLE_MSG, e.getClass().getCanonicalName(), e.getMessage(), e);

        ApiResponse<Object> response = getResponse(HttpStatus.BAD_REQUEST, e);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(EntityEmptyFieldException.class)
    protected ResponseEntity<ApiResponse<Object>> entityEmptyFieldExceptionHandler(EntityEmptyFieldException e) {

        log.error(LOG_EXCEPTION_HANDLE_MSG, e.getClass().getCanonicalName(), e.getMessage(), e);

        ApiResponse<Object> response = getResponse(HttpStatus.BAD_REQUEST, e);

        return ResponseEntity.status(response.getStatus()).body(response);

    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ApiResponse<Object>> noSuchElementExceptionHandle(NoSuchElementException e) {

        log.error(LOG_EXCEPTION_HANDLE_MSG, e.getClass().getCanonicalName(), e.getMessage(), e);

        ApiResponse resp = getResponse(HttpStatus.NOT_FOUND, e);

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ApiResponse<Object>> apiExceptionHandle(ApiException e) {

        log.error(LOG_EXCEPTION_HANDLE_MSG, e.getClass().getCanonicalName(), e.getMessage(), e);

        ApiResponse resp = getResponse(HttpStatus.BAD_REQUEST, e);

        log.debug("ApiException {}" + resp);

        return ResponseEntity.status(resp.getStatus()).body(resp);

    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Object>> notSupportedExceptionHandler(Exception e) {

        ApiResponse resp = getResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Неизвестная ошибка - " + e.getMessage(), e);

        log.debug("Неизвестная ошибка {}", resp, e);

        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    private ApiResponse<Object> getResponse(HttpStatus status, Exception e) {

        return ApiResponse.builder()
                .status(status)
                .message(e.getMessage())
                .debugMsg(getExceptionStackTrace(e))
                .build();

    }
    private ApiResponse<Object> getResponse(HttpStatus status, String msg, Exception e) {

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
