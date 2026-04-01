package com.trackorithm.track.modules.problem.entity;

import com.trackorithm.track.modules.subtopic.entity.Subtopic;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subtopic_problems",
        indexes = @Index(name = "idx_subtopic_problems_problem", columnList = "problem_id"))
@Getter
@Setter
public class SubtopicProblem {

    @EmbeddedId
    private SubtopicProblemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subtopicId")
    @JoinColumn(name = "subtopic_id", nullable = false)
    private Subtopic subtopic;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("problemId")
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}

