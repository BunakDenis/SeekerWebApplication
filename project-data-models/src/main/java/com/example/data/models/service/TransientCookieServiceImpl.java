package com.example.data.models.service;

import com.example.data.models.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class TransientCookieServiceImpl implements CookieService{


    @Value("${transient.auth.header.name}")
    private String headerName;

    @Value("${transient.auth.expiration.time}")
    private long expirationTime;

    @Override
    public void addToken(ServerHttpResponse response, String token) {
        CookieUtils.addToken(response, token, headerName, expirationTime);
    }

    @Override
    public void updateToken(ServerHttpResponse response, String token) {
        CookieUtils.updateToken(response, token, headerName, expirationTime);
    }

    @Override
    public String getToken(ServerHttpRequest request) {
        return CookieUtils.getToken(request, headerName);
    }

    @Override
    public void deactivateToken(ServerHttpResponse response) {
        CookieUtils.deactivateToken(response, headerName);
    }
}
