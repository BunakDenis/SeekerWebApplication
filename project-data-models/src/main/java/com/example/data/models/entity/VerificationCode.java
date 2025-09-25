package com.example.data.models.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class VerificationCode {

    private Long id;

    private String otpHash;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private Boolean isActive;

    private Integer attempts;

    @ToString.Exclude
    private User user;

}

