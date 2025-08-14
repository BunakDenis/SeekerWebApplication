package com.example.data.models.entity.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;


@Data
@Builder
public class CheckUserResponse {

    private boolean found;

    private byte access_level;

    private boolean active;

}
