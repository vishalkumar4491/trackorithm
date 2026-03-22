package com.trackorithm.track.modules.analytics.entity;

import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "daily_activity")
@Getter
@Setter
public class DailyActivity {
    @EmbeddedId
    private DailyActivityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    private Integer problemSolved = 0;

    private Integer timeSpent = 0;
}
