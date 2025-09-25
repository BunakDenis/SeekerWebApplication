package com.example.data.models.entity.jwt;


import lombok.Builder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

@Builder
public class JwtTelegramDataImpl implements JwtData {

    private UserDetails userDetails;

    private long expirationTime;

    private Map<String, Object> subjects;

    @Override
    public String getUsername() {
        return userDetails.getUsername();
    }

    @Override
    public long getExpirationTime() {
        return expirationTime;
    }

    @Override
    public Map<String, Object> getSubjects() {
        return Map.copyOf(subjects);
    }

    public void addSubject(String key, Object subject) {
        this.subjects.put(key, subject);
    }

    public Object getSubject(String key) {
        return this.subjects.get(key);
    }

}
