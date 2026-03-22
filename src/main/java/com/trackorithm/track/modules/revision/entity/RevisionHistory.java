package com.trackorithm.track.modules.revision.entity;


import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.modules.problem.entity.Problem;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "revision_schedule")
@Getter
@Setter
public class RevisionHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Problem problem;

    private Integer revisionNumber;

    private Integer confidenceLevel;

    private LocalDateTime reviewedAt;
}
