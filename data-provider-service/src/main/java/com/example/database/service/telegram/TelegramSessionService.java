package com.example.database.service.telegram;


import com.example.data.models.entity.dto.telegram.PersistentSessionDTO;
import com.example.data.models.entity.dto.telegram.TelegramSessionDTO;
import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TransientSessionDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.exception.EntityNotSavedException;
import com.example.database.entity.PersistentSession;
import com.example.database.entity.TelegramSession;
import com.example.database.entity.TelegramUser;
import com.example.database.entity.TransientSession;
import com.example.database.repo.jpa.telegram.TelegramSessionRepo;
import com.example.database.service.ModelMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramSessionService {

    private final TelegramSessionRepo repo;
    private final ModelMapperService mapperService;


    public ApiResponse<TelegramSessionDTO> save(TelegramSession session) {

        Optional<TelegramSession> save = Optional.of(repo.save(session));

        if (!save.isPresent()) throw new EntityNotSavedException(TelegramSession.class + " not saved");

        TelegramSession telegramSession = save.get();
        TelegramSession sessionWithTelegramUser = repo.findWithTelegramUserById(session.getId()).get();


        log.debug("Saved Telegram session {}", telegramSession);
        log.debug("Telegram user of saved telegram session {}", sessionWithTelegramUser.getTelegramUser());

        TelegramSessionDTO dto = mapperService.toDTO(telegramSession, TelegramSessionDTO.class);
        ApiResponse<TelegramSessionDTO> response = success(
                HttpStatus.CREATED,
                dto);

        TelegramUser telegramUser = sessionWithTelegramUser.getTelegramUser();
        TelegramUserDTO telegramUserDTO = mapperService.toDTO(telegramUser, TelegramUserDTO.class);

        response.addIncludeObject(ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(), telegramUserDTO);

        return response;
    }
    public ApiResponse<TelegramSessionDTO> update(TelegramSession session) {

        ApiResponse<TelegramSessionDTO> response = save(session);

        response.setStatus(HttpStatus.OK);

        return response;
    }
    public ApiResponse<TelegramSessionDTO> getSessionById(Long id) {
        Optional<TelegramSession> session = repo.findById(id);

        if (session.isPresent())
            return success(mapperService.toDTO(session.get(), TelegramSessionDTO.class));

        throw new EntityNotFoundException(TelegramSession.class + " not found", new TelegramSession());
    }
    public ApiResponse<TelegramSessionDTO> getByTelegramUserIdWithTelegramUser(Long telegramUserId) {

        Optional<TelegramSession> session = repo.getTelegramSessionByTelegramUserId(telegramUserId);

        log.debug("Found session {}", session);

        if (!session.isPresent())
            throw new EntityNotFoundException(TelegramSession.class + " not found", new TelegramSession());

        TelegramSession telegramSession = session.get();

        TelegramSessionDTO telegramSessionDTO = mapperService.toDTO(telegramSession, TelegramSessionDTO.class);

        TelegramUser telegramUser = telegramSession.getTelegramUser();
        TelegramUserDTO telegramUserDTO = mapperService.toDTO(telegramUser, TelegramUserDTO.class);

        ApiResponse<TelegramSessionDTO> response = success(telegramSessionDTO);

            response.addIncludeObject(
                    ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(),
                    telegramUserDTO
                    );

            return response;
    }
    public ApiResponse<Boolean> delete(Long id) {

        repo.deleteById(id);

        return success(Boolean.TRUE);
    }

}