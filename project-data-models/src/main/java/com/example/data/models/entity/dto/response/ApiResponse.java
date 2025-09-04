package com.example.data.models.entity.dto.response;

import com.example.data.models.consts.RequestMessageProvider;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm", timezone = "${default.time.zone.utc}")
    private LocalDateTime timestamp;

    @JsonIgnore
    @ToString.Exclude
    private HttpStatus status;

    private ApiResponse() {
        this.included = new LinkedHashMap<>();
        this.includedList = new LinkedHashMap<>();
        this.timestamp = LocalDateTime.now();
    }

    public void addIncludeObject(String key, Object object) {
        included.put(key, object);
    }
    public Object getIncludedObject(String key) {
        return included.get(key);
    }

    public void addIncludeListObjects(String key, List<Object> objectList) {
        includedList.put(key, objectList);
    }

    public List<Object> getIncludedListObjects(String key) {
        return includedList.get(key);
    }

    // ==== Builder ====

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private final ApiResponse<T> instance = new ApiResponse<>();

        public Builder<T> status(HttpStatus status) {
            instance.setStatus(status);
            return this;
        }

        public Builder<T> message(String message) {
            instance.setMessage(message);
            return this;
        }

        public Builder<T> data(T data) {
            instance.setData(data);
            return this;
        }

        public Builder<T> debugMsg(String debugMsg) {
            instance.setDebugMsg(debugMsg);
            return this;
        }

        public Builder<T> includeObject(String key, Object object) {
            instance.getIncluded().put(key, object);
            return this;
        }

        public Builder<T> includeList(String key, List<Object> objects) {
            instance.getIncludedList().put(key, objects);
            return this;
        }

        public ApiResponse<T> build() {
            // дефолты
            if (instance.getStatus() == null) {
                instance.setStatus(HttpStatus.OK);
            }
            if (instance.getMessage() == null) {
                instance.setMessage(RequestMessageProvider.SUCCESSES_MSG);
            }
            return instance;
        }
    }
}
