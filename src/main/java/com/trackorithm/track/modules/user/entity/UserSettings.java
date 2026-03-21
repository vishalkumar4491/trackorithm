package com.trackorithm.track.modules.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.usertype.UserType;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
public class UserSettings {

    @Id
    private UUID userId;

    @OneToOne
    @MapsId
    private User user;

    private String timezone;

    private String preferredLanguage;

    private Integer dailyGoal;

    private Boolean emailNotificationsEnabled;

    private LocalTime reminderTime;
}
