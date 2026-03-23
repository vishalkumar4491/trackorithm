package com.trackorithm.track.modules.problem.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "problem_editorials",
        uniqueConstraints = @UniqueConstraint(columnNames = {"problem_id"}),
        indexes = @Index(name = "idx_problem_editorials_problem", columnList = "problem_id"))
@Getter
@Setter
public class ProblemEditorial extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "youtube_url")
    private String youtubeUrl;

    @Column(name = "reference_url")
    private String referenceUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}

