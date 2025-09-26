package com.example.database.service.telegram;

import com.example.data.models.consts.ResponseMessageProvider;
import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.response.ApiResponse;
import com.example.data.models.enums.ResponseIncludeDataKeys;
import com.example.data.models.exception.EntityNotFoundException;
import com.example.data.models.exception.EntityNotSavedException;
import com.example.database.entity.TelegramSession;
import com.example.database.entity.User;
import com.example.database.entity.VerificationCode;
import com.example.database.repo.jpa.telegram.VerificationCodeRepo;
import com.example.database.service.ModelMapperService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.data.models.utils.ApiResponseUtilsService.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeService {


    private final VerificationCodeRepo verificationCodeRepo;
    private final ModelMapperService modelMapperService;
    private final ObjectMapper objectMapper;


    public ApiResponse<VerificationCodeDTO> save(VerificationCode code) {

        ApiResponse all = getAllActiveByUserId(code.getUser().getId());

        List includedListObjects = all.getIncludedListObjects(ResponseIncludeDataKeys.VERIFICATION_CODE.getKeyValue());

        if (!includedListObjects.isEmpty()) {

            log.debug("Перебор всех найденных кодов");

            includedListObjects.forEach(findCode -> {

                log.debug("find code {}", findCode);

                VerificationCode verificationCode = objectMapper.convertValue(findCode, VerificationCode.class);
                verificationCode.setUser(code.getUser());

                if (
                        Boolean.TRUE.equals(
                                verificationCode.getIsActive()
                        )
                ) {
                    verificationCode.setIsActive(false);
                    verificationCodeRepo.save(verificationCode);
                }

            });
        }

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

        throw new EntityNotFoundException(ResponseMessageProvider.getEntityNotFoundMessage(User.class), new User());
    }
    public ApiResponse<VerificationCodeDTO> getCodeByUserId(Long userId) {

        Optional<VerificationCode> code = verificationCodeRepo.findByUserId(userId);

        if (code.isPresent()) return success(
                    modelMapperService.toDTO(code, VerificationCodeDTO.class));

        throw new EntityNotFoundException(ResponseMessageProvider.getEntityNotFoundMessage(User.class), new User());

    }
    public ApiResponse<VerificationCodeDTO> getCodeByTelegramUserId(Long telegramUserId) {

        Optional<VerificationCode> code = verificationCodeRepo.findActiveByTelegramUserId(telegramUserId);

        if (code.isPresent()) return success(
                    modelMapperService.toDTO(code, VerificationCodeDTO.class)
        );

        throw new EntityNotFoundException(ResponseMessageProvider.getEntityNotFoundMessage(new User()), new User());
    }
    public ApiResponse<Object> getAllActiveByUserId(Long userId) {

        List<VerificationCode> all = verificationCodeRepo.findAllActiveByUserId(userId);

        if (all.isEmpty()) return ApiResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .includeList(ResponseIncludeDataKeys.VERIFICATION_CODE.getKeyValue(), Collections.EMPTY_LIST)
                .includeObject(ResponseIncludeDataKeys.USER.getKeyValue(),new UserDTO())
                .build();

        List dtoList = listEntityToDto(all);
        User user = all.get(0).getUser();
        UserDTO userDTO = modelMapperService.toDTO(user, UserDTO.class);

        return ApiResponse.<Object>builder()
                .status(HttpStatus.OK)
                .includeList(ResponseIncludeDataKeys.VERIFICATION_CODE.getKeyValue(), dtoList)
                .includeObject(ResponseIncludeDataKeys.USER.getKeyValue(), userDTO)
                .build();

    }
    public ApiResponse<Boolean> delete(Long id) {

        verificationCodeRepo.deleteById(id);
        return success(true);

    }
    private List<VerificationCodeDTO> listEntityToDto(List<VerificationCode> codeList) {

        List<VerificationCodeDTO> dtoList = new ArrayList<>();

        codeList.forEach(code -> dtoList.add(modelMapperService.toDTO(code, VerificationCodeDTO.class)));

        return List.copyOf(dtoList);

    }
    private List<VerificationCode> listDtoToEntity(List<VerificationCodeDTO> dtoList) {

        List<VerificationCode> codeList = new ArrayList<>();

        dtoList.forEach(code -> codeList.add(modelMapperService.toEntity(code, VerificationCode.class)));

        return List.copyOf(codeList);

    }

}
