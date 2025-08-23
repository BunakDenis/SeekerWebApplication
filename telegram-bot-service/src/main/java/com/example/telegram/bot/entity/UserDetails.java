package com.example.telegram.bot.entity;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDetails {

    private Long id;

    private String firstName;

    private String lastname;

    private LocalDateTime birthday;

    private String phoneNumber;

    private String gender;

    private String avatarLink;

    private String location;

    private LocalDateTime dateStartStudyingSchool;

    private String curator;

}
