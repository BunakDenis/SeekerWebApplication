package com.example.data.models.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class ApiResponseWithDataList<T> {

    private String message;

    private List<T> data;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonIgnore
    @ToString.Exclude
    private HttpStatus status;

    private ApiResponseWithDataList() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponseWithDataList(HttpStatus status) {
        this();
        this.status = status;
    }

    public ApiResponseWithDataList(HttpStatus status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public ApiResponseWithDataList(HttpStatus status, String message, List<T> data) {
        this();
        this.status = status;
        this.message = message;
        this.data = data;
    }

}
