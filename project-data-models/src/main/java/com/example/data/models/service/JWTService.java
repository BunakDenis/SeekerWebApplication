package com.example.data.models.service;


import com.example.data.models.entity.dto.jwt.JwtData;
import com.example.data.models.entity.dto.jwt.JwtTelegramDataImpl;
import com.example.utils.datetime.DateTimeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Function;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class JWTService {

    @Value("${token.signing.key}")
    private String key;
    @Value("${default.utc.zone.id}")
    private String zoneId;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeService.DATE_TIME_FORMAT);

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public LocalDateTime extractExpiration(String token) {

        Date date = extractClaim(token, Claims::getExpiration);

        return date
                .toInstant().atZone(ZoneId.of(zoneId))
                .toLocalDateTime();
    }
    public String extractExpirationByString(String token) {

        Date date = extractClaim(token, Claims::getExpiration);

        return date
                .toInstant().atZone(ZoneId.of(zoneId))
                .toLocalDateTime()
                .format(formatter);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token)
                .isBefore(LocalDateTime.now(ZoneId.of(zoneId)));
    }
    public String generateToken(JwtData jwtData) {
        return createToken(jwtData);
    }
    private String createToken(JwtData jwtData) {
        return Jwts.builder()
                .subject(jwtData.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtData.getExpirationTime()))
                .signWith(getSigningKey())
                .claims(jwtData.getSubjects())
                .compact();
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    private SecretKey getSigningKey() {
        byte[] decodeKey = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(decodeKey);
    }

}
