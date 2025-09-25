package com.example.data.models.entity.mysticschool;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleCategoryTitles {

    private int id;

    private String title;

    private LocalDateTime published_at;

}
