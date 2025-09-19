package com.example.data.models.exception;

import lombok.Data;

@Data
public class EntityNotFoundException extends IllegalArgumentException{

    private String entityClassName;

    public EntityNotFoundException(String msg, Object object) {
        super(msg);
        this.entityClassName = object.getClass().getSimpleName();
    }

}
