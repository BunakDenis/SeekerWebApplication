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
public class VerificationCodeDTO {

    private Long id;

    private String otpHash;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private Integer attempts;

}
