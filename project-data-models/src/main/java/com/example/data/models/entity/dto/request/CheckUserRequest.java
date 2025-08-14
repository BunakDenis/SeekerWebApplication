package com.example.data.models.entity.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckUserRequest {

    private String email;

}
