package com.example.data.models.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@Data
@ToString
public class ApiResponse<T> {

    private String message;

    private T data;

    private String debugMsg;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "${default.time.zone.utc}")
    private LocalDateTime timestamp;

    @JsonIgnore
    @ToString.Exclude
    private HttpStatus status;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(HttpStatus status) {
        this();
        this.status = status;
    }

    public ApiResponse(HttpStatus status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public ApiResponse(HttpStatus status, String message, T data) {
        this();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(HttpStatus status, String message, T data, String debugMsg) {
        this();
        this.status = status;
        this.message = message;
        this.data = data;
        this.debugMsg = debugMsg;
    }

}
