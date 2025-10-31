package com.example.database.api.controller;


import com.example.data.models.entity.dto.UserDTO;
import com.example.data.models.entity.dto.VerificationCodeDTO;
import com.example.data.models.entity.request.ApiRequest;
import com.example.data.models.entity.response.ApiResponse;
import com.example.database.entity.User;
import com.example.database.entity.VerificationCode;
import com.example.data.models.service.ModelMapperService;
import com.example.database.service.UserService;
import com.example.database.service.telegram.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeDataController {


    private final UserService userService;
    private final VerificationCodeService verificationCodeService;
    private final ModelMapperService mapperService;


    @PostMapping("/otp_code/add/")
    public ResponseEntity<ApiResponse<VerificationCodeDTO>> saveVerificationCode(
            @RequestBody ApiRequest<VerificationCodeDTO> request
    ) {
        log.debug("Запрос на сохранения VerificationCode {}", request);

        VerificationCode verificationCode = mapperService.toEntity(request.getData(), VerificationCode.class);

        User user = mapperService.toEntity(
                request.getIncludeObject("user"),
                User.class
        );

        ApiResponse<UserDTO> findUser = userService.getUserById(user.getId());

        userService.checkUser(mapperService.toEntity(findUser.getData(), User.class));

        verificationCode.setUser(user);

        ApiResponse<VerificationCodeDTO> response = verificationCodeService.save(verificationCode);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/otp_code/update/")
    public ResponseEntity<ApiResponse<VerificationCodeDTO>> updateVerificationCode(
            @RequestBody ApiRequest<VerificationCodeDTO> request
    ) {
        log.debug("Запрос на обновление VerificationCode {}", request);

        ApiResponse<VerificationCodeDTO> response = verificationCodeService.update(
                mapperService.toEntity(request.getData(), VerificationCode.class)
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/otp_code/{id}")
    public ResponseEntity<ApiResponse<VerificationCodeDTO>> getVerificationCodeById(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение VerificationCode по id {}", id);

        ApiResponse<VerificationCodeDTO> response = verificationCodeService.getCodeById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/otp_code/user_id/{id}")
    public ResponseEntity<ApiResponse<VerificationCodeDTO>> getVerificationCodeByUserId(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение VerificationCode по user_id {}", id);

        ApiResponse<VerificationCodeDTO> response = verificationCodeService.getCodeByUserId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/otp_code/telegram_user_id/{id}")
    public ResponseEntity<ApiResponse<VerificationCodeDTO>> getVerificationCodeByTelegramUserId(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на получение VerificationCode по telegram_user_id {}", id);

        ApiResponse<VerificationCodeDTO> response = verificationCodeService.getCodeByTelegramUserId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/otp_code/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteVerificationCode(
            @PathVariable("id") Long id
    ) {
        log.debug("Запрос на удаление VerificationCode с id {}", id);

        ApiResponse<Boolean> response = verificationCodeService.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
