package com.example.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "top_level_menus")
@Cacheable
public class TopLevelMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String commandKey;

    @Column(nullable = false)
    private String commandName;

    @OneToMany(mappedBy = "topLevelMenu", fetch = FetchType.LAZY)
    private List<SubLevelMenu> subLevelMenus;
}
