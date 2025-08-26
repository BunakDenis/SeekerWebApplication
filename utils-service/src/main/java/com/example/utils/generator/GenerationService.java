package com.example.utils.generator;

import lombok.Data;;


@Data
public class GenerationService {

    public static String generateEmailVerificationCode() {
        return VerificationCodeGenerator.generateVerificationCode();
    }

}
