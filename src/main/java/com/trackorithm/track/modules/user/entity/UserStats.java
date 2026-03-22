package com.trackorithm.track.modules.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_stats")
@Getter
@Setter
public class UserStats {
    @Id
    private UUID userId;

    @OneToOne
    @MapsId
    private User user;

    private Integer totalProblemsSolved;

    private Integer totalTimeSpent;

    private Integer currentStreak;

    private Integer maxStreak;

    private LocalDate lastActiveDate;
}
