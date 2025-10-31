package com.example.server.provider;

import org.apache.commons.collections.map.LinkedMap;

import java.util.Map;

public class ThymeleafModelObjectsProvider {

    public static Map<String, Object> getDefaultMapModelObjects() {
        Map<String, Object> result = new LinkedMap();
        result.put("notification_count", "5");
        return result;
    }

}
