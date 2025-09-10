package com.example.data.models.exception;

import lombok.Data;

@Data
public class EntityNotFoundException extends IllegalArgumentException{

    private Object object;

    public EntityNotFoundException(String msg, Object object) {
        super(msg);
        this.object = object;
    }

    /*
       TODO
            1. Вместо поля object добавить поле EntityClassName
     */

}
