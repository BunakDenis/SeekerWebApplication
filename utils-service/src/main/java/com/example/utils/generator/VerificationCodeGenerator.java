package com.example.utils.generator;

import lombok.Data;

import java.security.SecureRandom;

@Data
public class VerificationCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int CODE_LENGTH = 6;

    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a random alphanumeric verification code.
     *
     * @return A String representing the generated verification code.
     */
    public static String generateVerificationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            // Append a random character from the defined pool
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

}
