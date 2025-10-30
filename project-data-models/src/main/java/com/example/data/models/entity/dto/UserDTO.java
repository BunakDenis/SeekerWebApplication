package com.example.data.models.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String username;

    private String password;

    private String email;

    private String role;

    private Boolean active;

    private LocalDateTime registeredAt;

}
