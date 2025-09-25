package com.example.data.models.utils;

import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.consts.ResponseMessageProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ApiResponseUtilsService {

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK)
                .message(ResponseMessageProvider.SUCCESSES_MSG)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(ResponseMessageProvider.SUCCESSES_MSG)
                .data(data)
                .build();
    }

    public static ApiResponse<Object> fail(String msg) {
        return ApiResponse.<Object>builder()
                .message(msg)
                .build();
    }

    public static ApiResponse<Object> fail(String msg, String debugMsg) {
        return ApiResponse.<Object>builder()
                .message(msg)
                .debugMsg(debugMsg)
                .build();
    }

    public static ApiResponse<Object> fail(Object entity, String debugMsg) {
        return ApiResponse.<Object>builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ResponseMessageProvider.getEntityNotFoundMessage(entity))
                .debugMsg(debugMsg)
                .build();
    }

    public static ApiResponse<Object> fail(HttpStatus status, Object entity, String debugMsg) {
        return ApiResponse.<Object>builder()
                .status(status)
                .message(ResponseMessageProvider.getEntityNotFoundMessage(entity))
                .debugMsg(debugMsg)
                .build();
    }
}
