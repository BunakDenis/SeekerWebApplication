package com.example.data.models.consts;

import com.example.data.models.utils.EntityUtilsService;

import java.text.MessageFormat;

public class ExceptionMessageProvider {


    private static final String ENTITY_NULL_EXCEPTION_TEXT = "Entity {0} is null";
    private static final String ENTITY_EMPTY_FIELD_EXCEPTION_TEXT = "Field {0} is empty";
    private static final String ENTITY_NULL_FIELD_EXCEPTION_TEXT = "Field {0} is null";
    public static final String EXPIRED_PERSISTENT_SESSION = "Срок действия долгосрочной и краткосрочной сессии истёк.";
    private static final String NOT_ACTIVE_SESSION_MSG = "{0} with id={1} is not active";


    public static String getEntityNullExceptionText(Object object) {

        String entityName = EntityUtilsService.getEntityName(object);

        return MessageFormat.format(ENTITY_NULL_EXCEPTION_TEXT, entityName);
    }
    public static String getEntityNullFieldExceptionText(String fieldName) {
        return MessageFormat.format(ENTITY_NULL_FIELD_EXCEPTION_TEXT, fieldName);
    }
    public static String getEntityEmptyFieldExceptionText(String fieldName) {
        return MessageFormat.format(ENTITY_EMPTY_FIELD_EXCEPTION_TEXT, fieldName);
    }
    public static String getNotActiveSessionMsg(String sessionClassName, Long id) {
        return MessageFormat.format(NOT_ACTIVE_SESSION_MSG, sessionClassName, id);
    }

}
