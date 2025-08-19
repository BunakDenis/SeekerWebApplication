package com.example.data.models.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Data
@ToString
public class ApiResponse<T> {

    private String message;

    private T data;

    private Map<String, Object> included;

    private Map<String, List<Object>> includedList;

    private String debugMsg;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "${default.time.zone.utc}")
    private LocalDateTime timestamp;

    @JsonIgnore
    @ToString.Exclude
    private HttpStatus status;

    public ApiResponse() {
        this.included = new LinkedHashMap<>();
        this.includedList = new LinkedHashMap<>();
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

    public void addIncludeObject(String key, Object object) {
        included.put(key, object);
    }

    public Object getIncludeObject(String key) {
        return included.get(key);
    }

    public void addIncludeList(String key, List<Object> objects) {
        included.put(key, objects);
    }

    public List<Object> getIncludeList(String key) {
        return includedList.get(key);
    }

}
