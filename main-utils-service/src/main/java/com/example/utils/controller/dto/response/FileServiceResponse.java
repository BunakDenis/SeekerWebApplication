package com.example.utils.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class FileServiceResponse {

    private String result;

    private String message;

    @JsonIgnore
    private HttpStatus httpStatus;

    public FileServiceResponse(String msg, HttpStatus httpStatus) {
        this.message = msg;
        this.httpStatus = httpStatus;
    }

    public FileServiceResponse success(String result, String msg, HttpStatus httpStatus) {
        return new FileServiceResponse(result, msg, httpStatus);
    }

    public FileServiceResponse failed(String msg, HttpStatus httpStatus) {
        return new FileServiceResponse(msg, httpStatus);
    }

}
