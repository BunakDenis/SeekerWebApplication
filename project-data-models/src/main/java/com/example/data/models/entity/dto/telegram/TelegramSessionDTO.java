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

    private boolean isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "${default.time.zone.utc}")
    private LocalDateTime expirationTime;

}