package com.example.data.models.entity.dto.telegram;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelegramSessionDTO {

    private Long id;

    private String sessionData;

}