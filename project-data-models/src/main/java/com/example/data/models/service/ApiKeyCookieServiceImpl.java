package com.example.data.models.service;

import com.example.data.models.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiKeyCookieServiceImpl implements CookieService{

    @Value("${api.key.header.name}")
    private String apiKeyHeaderName;

    @Value("${api.key.token.expired.time}")
    private long apiKeyTokenExpiredTime;

    @Override
    public void addToken(ServerHttpResponse response, String token) {
        CookieUtils.addToken(response, token, apiKeyHeaderName, apiKeyTokenExpiredTime);
    }

    @Override
    public void updateToken(ServerHttpResponse response, String token) {
        CookieUtils.updateToken(response, token, apiKeyHeaderName, apiKeyTokenExpiredTime);
    }

    @Override
    public String getToken(ServerHttpRequest request) {
        return CookieUtils.getToken(request, apiKeyHeaderName);
    }

    @Override
    public void deactivateToken(ServerHttpResponse response) {
        CookieUtils.deactivateToken(response, apiKeyHeaderName);
    }
}
