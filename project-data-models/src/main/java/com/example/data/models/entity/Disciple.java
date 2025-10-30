package com.example.data.models.entity;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "curator"})
public class Disciple {

    private Long id;

    private User user;

    private Curator curator;

}
