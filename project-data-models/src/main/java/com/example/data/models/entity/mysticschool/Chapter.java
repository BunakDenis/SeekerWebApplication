package com.example.data.models.entity.mysticschool;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chapter {

    private int id;
    private String title;
    private String body;

}
