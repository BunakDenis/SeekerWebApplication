package com.example.telegram.bot.service;

import com.example.telegram.api.clients.DataProviderClient;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@RequiredArgsConstructor
@Builder
@Service
public class UserDetailsService {

    private final DataProviderClient dataProviderClient;

    private final ModelMapperService mapperService;

}
