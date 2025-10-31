package com.example.data.models.utils;


import org.springframework.util.StringUtils;

import java.util.Objects;

public class EntityUtilsService {

    public static String getEntityName(Object object) {
        return object.getClass().getSimpleName();
    }
    public static boolean isNull(Object object) { return Objects.isNull(object); }
    public static boolean isNullOrEmpty(String text) {
        return Objects.isNull(text) || text.isEmpty();
    }
    public static boolean isStringFieldEmpty(String fieldValue) {
        return !StringUtils.hasText(fieldValue);
    }

}
