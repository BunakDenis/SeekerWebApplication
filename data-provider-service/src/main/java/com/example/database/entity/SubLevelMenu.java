package com.example.database.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "sub_level_menus")
public class SubLevelMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String commandKey;

    @Column(nullable = false)
    private String commandName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top_level_menu_id", nullable = false)
    @ToString.Exclude
    private TopLevelMenu topLevelMenu;

    @OneToMany(mappedBy = "subLevelMenu", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<LeafLevelMenu> leafLevelMenus;
}
