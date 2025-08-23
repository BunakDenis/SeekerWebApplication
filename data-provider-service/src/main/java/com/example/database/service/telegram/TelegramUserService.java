package com.example.database.service.telegram;

import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.database.entity.TelegramUser;
import com.example.database.repo.telegram.TelegramUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.example.data.models.consts.RequestMessageProvider.NOT_FOUND_MSG;
import static com.example.data.models.consts.RequestMessageProvider.SUCCESSES_MSG;

@Service
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepo repo;

    private final ModelMapperService mapperService;

    public ApiResponse<TelegramUserDTO> getUserById(Long id) {

        TelegramUser user = repo.getTelegramUserById(id);

        TelegramUserDTO dto = mapperService.toDTO(user, TelegramUserDTO.class);

        if (Objects.nonNull(user)) {
            return new ApiResponse(HttpStatus.OK, SUCCESSES_MSG, dto);
        }

        return new ApiResponse<>(HttpStatus.BAD_REQUEST, NOT_FOUND_MSG);
    }

}
