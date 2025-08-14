package com.example.database.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "leaf_level_menus")
public class LeafLevelMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String commandKey;

    @Column(nullable = false)
    private String commandName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_level_menu_id", nullable = false)
    @ToString.Exclude
    private SubLevelMenu subLevelMenu;
}
