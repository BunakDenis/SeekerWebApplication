package com.example.data.models.consts;

import java.text.MessageFormat;

public class ResponseMessageProvider {

    public static final String SUCCESSES_MSG = "Successes!";
    public static final String ENTITY_NOT_FOUND_MSG = "Entity {0} not found in database";
    public static final String FAILED_TO_SAVE_ENTITY = "Fail save entity {0} to database";
    private static final String ENDPOINT_NOT_FOUND = "Endpoint {0} is not found";
    public static final String REQUEST_DO_NOT_CONTAIN_API_KEY = "Request don't contains Api Key";
    public static final String REQUEST_BODY_IS_EMPTY = "Request body is empty";
    public static final String REQUEST_BODY_DO_NOT_CONTAINS_TELEGRAM_UPDATE = "Request don't contains update";

    public static String getEntityNotFoundMessage(Object object) {
        return MessageFormat.format(ENTITY_NOT_FOUND_MSG, object.getClass().getName());
    }
    public static String getFailedToSaveEntity(Object object) {
        return MessageFormat.format(FAILED_TO_SAVE_ENTITY, object.getClass());
    }
    public static String getEndpointNotFoundMsg(String endpoint) {
        return MessageFormat.format(ENDPOINT_NOT_FOUND, endpoint);
    }

}
