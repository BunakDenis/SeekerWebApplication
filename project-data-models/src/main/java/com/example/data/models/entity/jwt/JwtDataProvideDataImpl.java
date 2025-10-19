package com.example.data.models.entity.jwt;

import lombok.Builder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Map;

@Builder
public class JwtDataProvideDataImpl implements JwtData {

    private String username;

    private long expirationTime;

    private Map<String, Object> subjects;

    @Override
    public String getUsername() {
        return String.valueOf(username);
    }

    @Override
    public long getExpirationTime() {
        return Long.valueOf(expirationTime);
    }

    @Override
    public Map<String, Object> getSubjects() {
        return Map.copyOf(subjects);
    }
}
