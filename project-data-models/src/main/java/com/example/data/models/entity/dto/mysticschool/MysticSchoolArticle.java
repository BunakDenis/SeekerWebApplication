package com.example.data.models.entity.dto.mysticschool;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MysticSchoolArticle {

    private int id;

    private String title;

    private LocalDateTime published_at;

    private String body;

}
