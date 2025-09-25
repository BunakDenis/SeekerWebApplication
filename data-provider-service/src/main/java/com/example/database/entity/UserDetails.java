package com.example.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "user")
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastname;

    @Column
    private LocalDate birthday;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column
    private String gender;

    @Column(name = "avatar_link")
    private String avatarLink;

    @Column
    private String location;

    @Column(name = "date_start_studying_school")
    private LocalDate dateStartStudyingSchool;

    @Column
    private String curator;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
}
