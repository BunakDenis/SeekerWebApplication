package com.example.database.service.telegram;

import com.example.data.models.consts.RequestMessageProvider;
import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.dto.response.ApiResponse;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.database.entity.VerificationCode;
import com.example.database.repo.telegram.VerificationCodeRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeService {

    private final VerificationCodeRepo verificationCodeRepo;
    private final ModelMapperService modelMapperService;
    public ApiResponse<VerificationCodeDTO> getCodeById(Long id) {
        Optional<VerificationCode> code = verificationCodeRepo.findById(id);

        if (code.isPresent()) {
            return new ApiResponse<>(
                    HttpStatus.OK,
                    RequestMessageProvider.SUCCESSES_MSG,
                    modelMapperService.toDTO(code, VerificationCodeDTO.class));
        }
        throw new EntityNotFoundException("Verification code with id=" + id + " is not found");
    }
    public ApiResponse<VerificationCodeDTO> getCodeByUserId(Long userId) {

        Optional<VerificationCode> code = verificationCodeRepo.findByUserId(userId);

        if (code.isPresent()) {
            return new ApiResponse<>(
                    HttpStatus.OK,
                    RequestMessageProvider.SUCCESSES_MSG,
                    modelMapperService.toDTO(code, VerificationCodeDTO.class));
        }
        throw new EntityNotFoundException("Verification code by user_id=" + userId + " is not found");

    }

    public ApiResponse<VerificationCodeDTO> getCodeByTelegramUserId(Long telegramUserId) {

        Optional<VerificationCode> code = verificationCodeRepo.findByTelegramUserId(telegramUserId);

        if (code.isPresent()) {
            return new ApiResponse<>(
                    HttpStatus.OK,
                    RequestMessageProvider.SUCCESSES_MSG,
                    modelMapperService.toDTO(code, VerificationCodeDTO.class));
        }
        throw new EntityNotFoundException("Verification code by telegram_user_id=" + telegramUserId + " is not found");

    }
    public ApiResponse<VerificationCodeDTO> save(VerificationCode code) {

        VerificationCode savedCode = verificationCodeRepo.save(code);

        return new ApiResponse<>(
                HttpStatus.CREATED,
                RequestMessageProvider.SUCCESSES_MSG,
                modelMapperService.toDTO(savedCode, VerificationCodeDTO.class));
    }
    public ApiResponse<VerificationCodeDTO> update(VerificationCode code) {

        VerificationCode updatedCode = verificationCodeRepo.save(code);

        return new ApiResponse<>(
                HttpStatus.OK,
                RequestMessageProvider.SUCCESSES_MSG,
                modelMapperService.toDTO(updatedCode, VerificationCodeDTO.class));
    }
    public ApiResponse<Boolean> delete(Long id) {
        try {
            verificationCodeRepo.deleteById(id);
            return new ApiResponse<>(HttpStatus.OK, RequestMessageProvider.SUCCESSES_MSG, true);
        } catch (EntityNotFoundException e) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, RequestMessageProvider.NOT_FOUND_MSG, false, e.getMessage());
        }
    }

    /*
            TODO Добавить:
                1. Проверки строка действия
                2. Автоудаление при достижении конца строка действия кода
     */

}
