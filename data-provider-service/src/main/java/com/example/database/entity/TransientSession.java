package com.example.database.entity;

import com.example.database.entity.telegram.TelegramSession;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transient_sessions")
@Cacheable
public class TransientSession {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "transient_session_data")
        private String data;

        @Column(name = "active", nullable = false)
        private boolean active;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "telegram_session_id", referencedColumnName = "id", nullable = false)
        @ToString.Exclude
        private TelegramSession telegramSession;

}
