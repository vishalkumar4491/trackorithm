package com.trackorithm.track.modules.sheet.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sheet_enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "sheet_id"}),
        indexes = @Index(name = "idx_use_user_active", columnList = "user_id"))
@Getter
@Setter
public class UserSheetEnrollment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sheet_id", nullable = false)
    private Sheet sheet;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt = LocalDateTime.now();

    @Column(name = "removed_at")
    private LocalDateTime removedAt;
}

