package com.example.data.models.entity.dto;

import com.example.data.models.entity.dto.telegram.TelegramUserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String email;

    private String role;

    private Boolean isActive;

    private List<TelegramUserDTO> telegramUsers;

}
