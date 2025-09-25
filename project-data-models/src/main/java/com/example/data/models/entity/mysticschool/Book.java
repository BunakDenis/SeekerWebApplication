package com.example.data.models.entity.mysticschool;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

      private int id;
      private String title;
      private String full_description;
      private String publisher;
      private int pages;
      private int year;
      private String cover;
      private List<BookContent> contents;

}
