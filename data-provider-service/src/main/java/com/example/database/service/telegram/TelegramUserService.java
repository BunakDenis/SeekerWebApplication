package com.example.database.service.telegram;

import com.example.data.models.consts.RequestMessageProvider;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.database.entity.TelegramUser;
import com.example.database.entity.User;
import com.example.database.repo.telegram.TelegramUserRepo;
import com.example.database.service.ModelMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.example.data.models.consts.RequestMessageProvider.*;
import static com.example.data.models.utils.ApiResponseUtilsService.*;

@Service
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepo repo;
    private final ModelMapperService mapperService;


    public ApiResponse<TelegramUserDTO> getUserById(Long id) {

        TelegramUser user = repo.getTelegramUserById(id);

        if (Objects.nonNull(user))
            return success(mapperService.toDTO(user, TelegramUserDTO.class));

        throw new EntityNotFoundException(
                RequestMessageProvider.getEntityNotFoundMessage(User.class),
                new TelegramUser()
        );
    }

}
