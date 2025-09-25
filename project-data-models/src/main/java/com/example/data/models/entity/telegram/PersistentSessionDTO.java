package com.example.data.models.entity.telegram;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersistentSessionDTO {

    private Long id;

    private String data;

    private boolean isActive;

}
