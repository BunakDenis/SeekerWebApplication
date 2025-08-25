package com.example.data.models.entity.dto.request;

import com.example.data.models.entity.dto.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Data
public class ApiRequest<T> {

    private T data;

    private Map<String, Object> included;

    private Map<String, List<Object>> includedList;

    public ApiRequest() {
        this.included = new LinkedHashMap<>();
        this.includedList = new LinkedHashMap<>();
    }

    public ApiRequest(T data) {
        this();
        this.data = data;
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
