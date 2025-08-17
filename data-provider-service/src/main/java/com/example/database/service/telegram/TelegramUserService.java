package com.example.database.service.telegram;

import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.database.entity.TelegramSession;
import com.example.database.entity.TelegramUser;
import com.example.database.repo.telegram.TelegramUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.example.database.consts.RequestMessageProvider.NOT_FOUND_MSG;
import static com.example.database.consts.RequestMessageProvider.SUCCESSES_MSG;

@Service
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepo repo;

    public ApiResponse<TelegramUserDTO> getUserById(Long id) {

        TelegramUser user = repo.getTelegramUserById(id);

        TelegramUserDTO dto = toDto(user);

        if (Objects.nonNull(user)) {
            return new ApiResponse(HttpStatus.OK, SUCCESSES_MSG, dto);
        }

        return new ApiResponse<>(HttpStatus.BAD_REQUEST, NOT_FOUND_MSG);
    }

    public TelegramUser toEntity(TelegramUserDTO dto) {

        TelegramSessionDTO telegramSession = dto.getTelegramSession();

        TelegramSession session = TelegramSession.builder()
                .id(telegramSession.getId())
                .isActive(telegramSession.isActive())
                .expirationTime(telegramSession.getExpirationTime())
                .sessionData(telegramSession.getSessionData())
                .build();

        return TelegramUser.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .isActive(dto.isActive())
                .telegramSession(session)
                .build();
    }

    public TelegramUserDTO toDto(TelegramUser user) {

        TelegramSession telegramSession = user.getTelegramSession();

        return TelegramUserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .isActive(user.isActive())
                .telegramSession(
                        new TelegramSessionDTO(
                                telegramSession.getId(), telegramSession.getSessionData(),
                                telegramSession.isActive(), telegramSession.getExpirationTime()
                        )
                )
                .build();
    }

}
