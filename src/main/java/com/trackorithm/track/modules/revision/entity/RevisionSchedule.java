package com.trackorithm.track.modules.revision.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.common.enums.RevisionStatus;
import com.trackorithm.track.modules.problem.entity.Problem;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "revision_schedule",
        indexes = @Index(name = "idx_revision_user_date", columnList = "user_id, scheduled_date, status"))
@Getter
@Setter
public class RevisionSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Problem problem;

    private Integer revisionNumber;

    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "revision_status_enum")
    private RevisionStatus status;

    private LocalDateTime completedAt;
}
