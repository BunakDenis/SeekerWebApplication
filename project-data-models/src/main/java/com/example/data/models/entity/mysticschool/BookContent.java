package com.example.data.models.entity.mysticschool;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookContent {

    private int id;
    private int parent_id;
    private String title;
    private int chapter_id;

}
