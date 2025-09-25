package com.example.data.models.entity.jwt;

import java.util.Map;

public interface JwtData {

    String getUsername();

    long getExpirationTime();

    Map<String, Object> getSubjects();

}
