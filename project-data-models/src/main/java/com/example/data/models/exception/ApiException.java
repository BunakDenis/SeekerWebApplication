package com.example.data.models.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class ApiException extends RuntimeException {

    private String message;

    public ApiException(String msg) {
        super(msg);
    }

}
