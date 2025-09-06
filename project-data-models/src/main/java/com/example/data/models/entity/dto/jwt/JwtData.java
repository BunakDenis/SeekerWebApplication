package com.example.data.models.entity.dto.jwt;

import java.util.Map;

public interface JwtData {

    String getUsername();

    long getExpirationTime();

    Map<String, Object> getSubjects();

}
