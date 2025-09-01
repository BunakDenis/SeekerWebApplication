package com.example.database.service.telegram;

import com.example.data.models.consts.RequestMessageProvider;
import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.exception.EntityNotSavedException;
import com.example.database.entity.TelegramSession;
import com.example.database.entity.User;
import com.example.database.entity.VerificationCode;
import com.example.database.repo.telegram.VerificationCodeRepo;
import com.example.database.service.ModelMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeService {

    private final VerificationCodeRepo verificationCodeRepo;
    private final ModelMapperService modelMapperService;

    public ApiResponse<VerificationCodeDTO> save(VerificationCode code) {

        Optional<VerificationCode> save = Optional.of(verificationCodeRepo.save(code));

        if (save.isPresent()) return success(
                HttpStatus.CREATED,
                modelMapperService.toDTO(save, VerificationCodeDTO.class)
        );

        throw new EntityNotSavedException(TelegramSession.class + " not saved");

    }
    public ApiResponse<VerificationCodeDTO> update(VerificationCode code) {

        ApiResponse<VerificationCodeDTO> response = save(code);
        response.setStatus(HttpStatus.OK);

        return response;
    }
    public ApiResponse<VerificationCodeDTO> getCodeById(Long id) {
        Optional<VerificationCode> code = verificationCodeRepo.findById(id);

        if (code.isPresent()) return success(
                    modelMapperService.toDTO(code, VerificationCodeDTO.class));

        throw new EntityNotFoundException(RequestMessageProvider.getEntityNotFoundMessage(User.class), new User());
    }
    public ApiResponse<VerificationCodeDTO> getCodeByUserId(Long userId) {

        Optional<VerificationCode> code = verificationCodeRepo.findByUserId(userId);

        if (code.isPresent()) return success(
                    modelMapperService.toDTO(code, VerificationCodeDTO.class));

        throw new EntityNotFoundException(RequestMessageProvider.getEntityNotFoundMessage(User.class), new User());

    }
    public ApiResponse<VerificationCodeDTO> getCodeByTelegramUserId(Long telegramUserId) {

        Optional<VerificationCode> code = verificationCodeRepo.findByTelegramUserId(telegramUserId);

        if (code.isPresent()) return success(
                    modelMapperService.toDTO(code, VerificationCodeDTO.class)
        );

        throw new EntityNotFoundException(RequestMessageProvider.getEntityNotFoundMessage(User.class), new User());
    }
    public ApiResponse<Boolean> delete(Long id) {

        verificationCodeRepo.deleteById(id);
        return success(true);

    }

}
