package com.example.database.service.telegram;


import com.example.data.models.entity.telegram.PersistentSessionDTO;
import com.example.data.models.entity.telegram.TelegramSessionDTO;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.telegram.TransientSessionDTO;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.exception.EntityNotSavedException;
import com.example.database.entity.PersistentSession;
import com.example.database.entity.TelegramSession;
import com.example.database.entity.TelegramUser;
import com.example.database.entity.TransientSession;
import com.example.database.repo.telegram.TelegramSessionRepo;
import com.example.database.service.ModelMapperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

@Service
@RequiredArgsConstructor
public class TelegramSessionService {

    private final TelegramSessionRepo repo;
    private final ModelMapperService mapperService;


    public ApiResponse<TelegramSessionDTO> create(TelegramSession session) {

        Optional<TelegramSession> save = Optional.of(repo.save(session));

        if (save.isPresent()) return success(
                    HttpStatus.CREATED,
                    mapperService.toDTO(save, TelegramSessionDTO.class)
            );

        throw new EntityNotSavedException(TelegramSession.class + " not saved");
    }
    public ApiResponse<TelegramSessionDTO> update(TelegramSession session) {

        ApiResponse<TelegramSessionDTO> response = create(session);

        response.setStatus(HttpStatus.OK);

        return response;
    }
    public ApiResponse<TelegramSessionDTO> getSessionById(Long id) {
        Optional<TelegramSession> session = repo.findById(id);

        if (session.isPresent())
            return success(mapperService.toDTO(session.get(), TelegramSessionDTO.class));

        throw new EntityNotFoundException(TelegramSession.class + " not found", new TelegramSession());
    }
    @Transactional
    public ApiResponse<TelegramSessionDTO> getByTelegramUserId(Long telegramUserId) {

        Optional<TelegramSession> session = repo.getTelegramSessionByTelegramUserId(telegramUserId);

        if (session.isPresent()) {

            TelegramSession telegramSession = session.get();
            TelegramSessionDTO telegramSessionDTO = mapperService.toDTO(telegramSession, TelegramSessionDTO.class);

            TelegramUser telegramUser = telegramSession.getTelegramUser();
            List<PersistentSession> persistentSessions = telegramSession.getPersistentSessions();
            List<TransientSession> transientSessions = telegramSession.getTransientSessions();

            List<Object> prSessionDTOList = new ArrayList<>();
            List<Object> trSessionDTOList = new ArrayList<>();

            persistentSessions.forEach(prSession ->
                    prSessionDTOList.add(mapperService.toDTO(prSession, PersistentSessionDTO.class)));

            transientSessions.forEach(trSession ->
                    trSessionDTOList.add(mapperService.toDTO(trSession, TransientSessionDTO.class)));

            ApiResponse<TelegramSessionDTO> response = success(telegramSessionDTO);

            response.addIncludeObject(
                    ResponseIncludeDataKeys.TELEGRAM_USER.getKeyValue(),
                    mapperService.toDTO(telegramUser, TelegramUser.class)
            );
            response.addIncludeListObjects(ResponseIncludeDataKeys.PERSISTENT_SESSION.getKeyValue(), prSessionDTOList);
            response.addIncludeListObjects(ResponseIncludeDataKeys.TRANSIENT_SESSION.getKeyValue(), trSessionDTOList);

            return response;
        }

        throw new EntityNotFoundException(TelegramSession.class + " not found", new TelegramSession());

    }
    public ApiResponse<Boolean> delete(Long id) {

        repo.deleteById(id);

        return success(Boolean.TRUE);
    }

}
