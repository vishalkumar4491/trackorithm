package com.trackorithm.track.modules.tracking.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.common.enums.ProblemStatus;
import com.trackorithm.track.modules.problem.entity.Problem;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_problem_status",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "problem_id"}),
        indexes = {
                @Index(name = "idx_ups_user_status", columnList = "user_id, status"),
                @Index(name = "idx_ups_user_last_solved", columnList = "user_id, last_solved_at")
        })
@Getter
@Setter
public class UserProblemStatus extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Problem problem;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "problem_status_enum")
    private ProblemStatus status;

    private Integer attemptCount;

    private Integer successCount;

    private Integer confidenceLevel;

    private LocalDateTime firstAttemptAt;

    private LocalDateTime lastAttemptAt;

    private LocalDateTime lastSolvedAt;

    private LocalDateTime lastRevisionAt;

    private LocalDateTime lastViewedAt;

    private Integer viewCount;

    private Boolean isVerified;

    private Boolean isBookmarked;

    private String source;
}
