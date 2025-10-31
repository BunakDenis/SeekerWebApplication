package com.example.database.service.telegram;

import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.database.entity.telegram.TelegramSession;
import com.example.database.entity.telegram.TelegramUser;
import com.example.database.repo.jpa.telegram.TelegramUserRepo;
import com.example.data.models.service.ModelMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

@Service
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepo repo;
    private final ModelMapperService mapperService;


    public ApiResponse<TelegramUserDTO> save(TelegramUser telegramUser) {

        TelegramUser savedUser = repo.save(telegramUser);

        return success(mapperService.toDTO(savedUser, TelegramUserDTO.class));

    }

    public ApiResponse<TelegramUserDTO> update(TelegramUser telegramUser) {
        return save(telegramUser);
    }

    public ApiResponse<TelegramUserDTO> getByTelegramUserId(Long telegramUserId) {

        Optional<TelegramUser> optionalUser = repo.findByTelegramUserId(telegramUserId);

        if (optionalUser.isPresent()) {
            TelegramUser user = optionalUser.get();

            return success(mapperService.toDTO(user, TelegramUserDTO.class));
        }

        throw new EntityNotFoundException("Telegram user with id=" + telegramUserId + " is not found", TelegramUser.class);

    }

    public ApiResponse<TelegramUserDTO> getByTelegramUserIdWithTelegramSession(Long telegramUserId) {

        Optional<TelegramUser> optionalUser = repo.findByTelegramUserIdWithTelegramSessionsDesc(telegramUserId);

        if (!optionalUser.isPresent())
            throw new EntityNotFoundException("Telegram user with id=" + telegramUserId + " is not found", TelegramUser.class);


        TelegramUser user = optionalUser.get();

        TelegramSession telegramSession = user.getTelegramSessions().get(0);
        TelegramSessionDTO telegramSessionDTO = mapperService.toDTO(telegramSession, TelegramSessionDTO.class);

        ApiResponse<TelegramUserDTO> response = success(mapperService.toDTO(user, TelegramUserDTO.class));
        response.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_SESSION.getKeyValue(), telegramSessionDTO);

        return response;
    }

    public ApiResponse<Boolean> delete(Long id) {
        repo.deleteById(id);

        return ApiResponseUtilsService.success(true);
    }

}
