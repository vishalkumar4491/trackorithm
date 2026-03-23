package com.trackorithm.track.modules.tracking.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.common.enums.AttempResult;
import com.trackorithm.track.modules.problem.entity.Problem;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "problem_attempts",
        indexes = @Index(name = "idx_attempts_user_problem", columnList = "user_id, problem_id"))
@Getter
@Setter
public class ProblemAttempt extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Problem problem;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "result", columnDefinition = "attempt_result_enum")
    private AttempResult result;

    private Integer timeSpentSeconds;

    private LocalDateTime submittedAt;

}
