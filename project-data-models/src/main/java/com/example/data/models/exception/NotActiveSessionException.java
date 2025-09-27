package com.example.data.models.exception;

public class NotActiveSessionException extends NullPointerException {

    public NotActiveSessionException(String msg) {
        super(msg);
    }

}
