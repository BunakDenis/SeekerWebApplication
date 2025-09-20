package com.example.data.models.exception;


import lombok.Data;


@Data
public class EntityNotSavedException extends IllegalArgumentException {

    public EntityNotSavedException(String msg) {
        super(msg);
    }

}
