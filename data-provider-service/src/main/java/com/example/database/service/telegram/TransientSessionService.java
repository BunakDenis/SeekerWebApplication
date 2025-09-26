package com.example.database.service.telegram;


import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.entity.dto.telegram.TransientSessionDTO;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.utils.ApiResponseUtilsService;
import com.example.database.entity.TransientSession;
import com.example.database.repo.jpa.telegram.TransientSessionRepo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Data
public class TransientSessionService {

    private final TransientSessionRepo repo;

    private final ModelMapper modelMapper;

    public ApiResponse<TransientSessionDTO> save(TransientSession session) {

        TransientSession saveSession = repo.save(session);

        return ApiResponseUtilsService.success(modelMapper.map(saveSession, TransientSessionDTO.class));

    }

    public ApiResponse<TransientSessionDTO> update(TransientSession session) {
        return save(session);
    }

    public ApiResponse<TransientSessionDTO> getById(Long id) {

        Optional<TransientSession> optionalSession = repo.findById(id);

        if (optionalSession.isPresent()) {

            TransientSession session = optionalSession.get();

            return ApiResponseUtilsService.success(modelMapper.map(session, TransientSessionDTO.class));
        }

        throw new EntityNotFoundException("Transient session with id=" + id + " is not found", TransientSession.class);

    }

    public ApiResponse<Boolean> delete(Long id) {

        repo.deleteById(id);

        return ApiResponseUtilsService.success(true);

    }

}
