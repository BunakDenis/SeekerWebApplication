package com.example.data.models.utils;


import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class CookieUtils {

    public static String getToken(ServerHttpRequest request, String headerName) {
        HttpCookie cookie = request.getCookies().getFirst(headerName);
        return cookie != null ? cookie.getValue() : "";
    }

    public static void updateToken(ServerHttpResponse response, String token, String headerName, long expirationTime) {
        setExpirationTimeByZeroToToken(response, headerName);
        addToken(response, token, headerName, expirationTime);
    }

    public static void addToken(ServerHttpResponse response, String token, String headerName, long expirationTime) {
        ResponseCookie cookie = ResponseCookie.from(headerName, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(expirationTime)
                .build();

        response.getHeaders().add("Set-Cookie", cookie.toString());
    }

    public static void deactivateToken(ServerHttpResponse response, String headerName) {
        setExpirationTimeByZeroToToken(response, headerName);
    }

    private static void setExpirationTimeByZeroToToken(ServerHttpResponse response, String headerName) {
        ResponseCookie expiredCookie = ResponseCookie.from(headerName, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        response.getHeaders().add("Set-Cookie", expiredCookie.toString());
    }

}
