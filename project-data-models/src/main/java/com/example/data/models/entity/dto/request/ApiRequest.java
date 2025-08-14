package com.example.data.models.entity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequest<T> {

    private T data;

}
