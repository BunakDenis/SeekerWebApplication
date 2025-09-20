package com.example.data.models.exception;

import com.example.data.models.consts.ExceptionMessageProvider;
import lombok.Data;

@Data
public class EntityEmptyFieldException extends IllegalArgumentException {

    private String message;

    public EntityEmptyFieldException(String entityFieldName) {
        super(entityFieldName);
        this.message = ExceptionMessageProvider.getEntityEmptyFieldExceptionText(entityFieldName);
    }

}
