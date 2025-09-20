package com.example.data.models.exception;


import com.example.data.models.consts.ExceptionMessageProvider;
import lombok.Data;

@Data
public class EntityNullFieldException extends NullPointerException{

    private String message;

    public EntityNullFieldException(String fieldName) {
        super(fieldName);
        this.message = ExceptionMessageProvider.getEntityNullFieldExceptionText(fieldName);
    }

}
