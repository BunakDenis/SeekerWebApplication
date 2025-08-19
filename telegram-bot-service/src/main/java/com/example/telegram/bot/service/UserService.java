package com.example.telegram.bot.service;

import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.UserDetailsDTO;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.telegram.api.clients.DataProviderClient;
import com.example.telegram.bot.entity.TelegramUser;
import com.example.telegram.bot.entity.User;
import com.example.telegram.bot.entity.UserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.data.models.enums.ResponseIncludeDataKeys.TELEGRAM_USER;
import static com.example.data.models.enums.ResponseIncludeDataKeys.USER_DETAILS;

@Data
@RequiredArgsConstructor
@Builder
@Service
public class UserService {

    private final DataProviderClient dataProviderClient;

    private final ModelMapperService mapperService;

    private final ObjectMapper objectMapper;

    public Mono<ApiResponse<UserDTO>> getUser(Long id) {
        return dataProviderClient.getUser(id);
    }

    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserId(Long id) {
        return dataProviderClient.getUserByTelegramUserId(id);
    }

    public Mono<ApiResponse<UserDTO>> getUserByTelegramUserIdWithUserDetails(Long id) {
        return dataProviderClient.getUserByTelegramUserIdWithUserDetails(id);
    }

    public User toEntity(ApiResponse<UserDTO> dto) {

        UserDetails userDetails = new UserDetails();
        TelegramUser telegramUser = new TelegramUser();
        List<TelegramUser> telegramUsers = new ArrayList<>();


        User result = mapperService.toEntity(dto.getData(), User.class);

        Object userDetailsDTOObject = dto.getIncludeObject(USER_DETAILS.getKeyValue());

        Object telegramUserDTO = dto.getIncludeObject(TELEGRAM_USER.getKeyValue());

        List<Object> telegramUserDTOList = dto.getIncludeList(TELEGRAM_USER.getKeyValue());


        if (Objects.nonNull(userDetailsDTOObject)) {
            userDetails = mapperService.toEntity(
                    objectMapper.convertValue(userDetailsDTOObject, UserDetailsDTO.class),
                    UserDetails.class
            );

            result.setUserDetails(userDetails);
        }

        if (Objects.nonNull(telegramUserDTO)) {
            telegramUser = mapperService.toEntity(
                    objectMapper.convertValue(telegramUserDTO, TelegramUserDTO.class),
                    TelegramUser.class
            );

            result.setTelegramUsers(List.of(telegramUser));
        }

        if (telegramUserDTOList != null && !telegramUserDTOList.isEmpty()) {
            telegramUserDTOList.forEach(tuDTO -> {
                telegramUsers.add(
                        mapperService.toEntity(
                                objectMapper.convertValue(tuDTO, TelegramUserDTO.class),
                                TelegramUser.class
                        )
                );
            });

            result.setTelegramUsers(telegramUsers);
        }

        return result;
    }

}
