package com.trackorithm.track.modules.analytics.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.common.enums.ActivityType;
import com.trackorithm.track.modules.problem.entity.Problem;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "activity_log",
        indexes = @Index(name = "idx_activity_user_time", columnList = "user_id, created_at"))
@Getter
@Setter
public class ActivityLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "action_type", columnDefinition = "activity_type_enum")
    private ActivityType activityType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Problem problem;

    @Column(columnDefinition = "jsonb")
    private String metadata;
}
