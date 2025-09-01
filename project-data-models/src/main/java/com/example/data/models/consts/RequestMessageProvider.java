package com.example.data.models.consts;

import java.text.MessageFormat;

public class RequestMessageProvider {

    public static final String SUCCESSES_MSG = "Successes!";

    public static final String ENTITY_NOT_FOUND_MSG = "Entity {0} not found in database";

    public static final String FAILED_TO_SAVE_ENTITY = "Fail save entity {0} to database";

    public static String getEntityNotFoundMessage(Object object) {
        return MessageFormat.format(ENTITY_NOT_FOUND_MSG, object.getClass());
    }

    public static String getFailedToSaveEntity(Object object) {
        return MessageFormat.format(FAILED_TO_SAVE_ENTITY, object.getClass());
    }

}
