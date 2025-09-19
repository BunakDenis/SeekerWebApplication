package com.example.database.service.telegram;

import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.database.entity.TelegramUser;
import com.example.database.repo.telegram.TelegramUserRepo;
import com.example.database.service.ModelMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepo repo;
    private final ModelMapperService mapperService;


    public ApiResponse<TelegramUserDTO> save(TelegramUser telegramUser) {

        TelegramUser savedUser = repo.save(telegramUser);

        return ApiResponseUtilsService.success(mapperService.toDTO(savedUser, TelegramUserDTO.class));

    }
    public ApiResponse<TelegramUserDTO> update(TelegramUser telegramUser) {
        return save(telegramUser);
    }
    public ApiResponse<TelegramUserDTO> getById(Long id) {
        Optional<TelegramUser> optionalUser = repo.findById(id);

        if (optionalUser.isPresent()) {
            TelegramUser user = optionalUser.get();

            return ApiResponseUtilsService.success(mapperService.toDTO(user, TelegramUserDTO.class));
        }

        throw new EntityNotFoundException("Telegram user with id=" + id + " is not found", TelegramUser.class);

    }
    public ApiResponse<Boolean> delete(Long id) {
        repo.deleteById(id);

        return ApiResponseUtilsService.success(true);
    }

}
