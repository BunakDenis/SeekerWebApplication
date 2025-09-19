package com.example.database.filter;


import com.example.data.models.consts.ResponseMessageProvider;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.service.ApplicationFiltersService;
import com.example.data.models.service.JWTService;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.database.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;


@Component
@RequiredArgsConstructor
@Slf4j
public class DataProviderServiceAuthFilter implements WebFilter {

    @Value("${api.key.header.name}")
    private String apiKeyHeaderName;
    private final ApplicationFiltersService appFiltersService;
    private final UserService userService;
    private final JWTService jwtService;
    private final ModelMapper mapper;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        log.debug("DataProviderServiceAuthFilter invoked for {}", request.getURI());

        List<String> apiKeyValue = headers.get(apiKeyHeaderName);

        String apiKey = Objects.isNull(apiKeyValue) ? "" : apiKeyValue.get(0);

        if (StringUtils.hasText(apiKey)) {

            String username = jwtService.extractUsername(apiKey);

            // Авторизуем как default user
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (userDetails == null) {
                log.warn("UserDetails is null - skipping auth");
                return chain.filter(exchange);
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            log.debug("ApiKey header found -> authenticating as user: {}", userDetails.getUsername());

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        }

        ApiResponse<Object> response = ApiResponseUtilsService.fail(
                ResponseMessageProvider.REQUEST_DO_NOT_CONTAIN_API_KEY
        );

        log.debug("No api-key header found -> proceed without authentication");

        return appFiltersService.writeJsonErrorResponse(
                exchange.getResponse(),
                HttpStatus.UNAUTHORIZED,
                response
        );

    }

}
