package com.example.data.models.entity;

import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "disciples"})
public class Curator {

    private Long id;

    private User user;

    private List<Disciple> disciples;
}
