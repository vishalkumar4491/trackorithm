package com.trackorithm.track.modules.notes.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.modules.problem.entity.Problem;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "problem_id"}),
        indexes = {
                @Index(name = "idx_notes_user_poblem", columnList = "user_id, problem_id")
        })
@Setter
public class Note extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Problem problem;
}
