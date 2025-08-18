package com.example.data.models.entity.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDetailsDTO {

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
