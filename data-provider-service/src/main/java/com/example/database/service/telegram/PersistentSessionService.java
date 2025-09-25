package com.example.database.service.telegram;


import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.PersistentSessionDTO;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.database.entity.PersistentSession;
import com.example.database.repo.telegram.PersistentSessionRepo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Data
public class PersistentSessionService {

    private final PersistentSessionRepo repo;

    private final ModelMapper mapperService;

    public ApiResponse<PersistentSessionDTO> save(PersistentSession session) {

        PersistentSession savedSession = repo.save(session);

        return ApiResponseUtilsService.success(mapperService.map(savedSession, PersistentSessionDTO.class));

    }
    public ApiResponse<PersistentSessionDTO> update(PersistentSession session) {
        return save(session);
    }
    public ApiResponse<PersistentSessionDTO> getById(Long id) {
        Optional<PersistentSession> optionalSession = repo.findById(id);

        if (optionalSession.isPresent()) {
            PersistentSession result = optionalSession.get();

            return ApiResponseUtilsService.success(mapperService.map(result, PersistentSessionDTO.class));
        }

        throw new EntityNotFoundException("Persistent session with id=" + id + " is not found", PersistentSession.class);

    }
    public ApiResponse<Boolean> delete(Long id) {
        repo.deleteById(id);

        return ApiResponseUtilsService.success(true);
    }

}
