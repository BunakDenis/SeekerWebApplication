package com.example.data.models.entity.dto.telegram;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersistentSessionDTO {

    private Long id;

    private String data;

    private boolean active;

}
