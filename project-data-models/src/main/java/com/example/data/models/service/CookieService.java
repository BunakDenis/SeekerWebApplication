package com.example.data.models.service;

import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;

public interface CookieService {

    void addToken(ServerHttpResponse response, String token);

    void updateToken(ServerHttpResponse response, String token);

    String getToken(ServerHttpRequest request);

    void deactivateToken(ServerHttpResponse response);

}
