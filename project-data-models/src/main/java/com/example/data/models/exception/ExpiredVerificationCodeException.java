package com.example.data.models.exception;


public class ExpiredVerificationCodeException extends Exception {


    public ExpiredVerificationCodeException(String message) {
        super(message);
    }

}
