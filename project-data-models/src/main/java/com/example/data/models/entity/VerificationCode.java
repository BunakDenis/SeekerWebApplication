package com.example.data.models.entity;

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

    private Integer attempts;

    @ToString.Exclude
    private User user;

}

