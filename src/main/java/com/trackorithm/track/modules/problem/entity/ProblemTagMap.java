package com.trackorithm.track.modules.problem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "problem_tag_map")
@Getter
@Setter
public class ProblemTagMap {

    @EmbeddedId
    private ProblemTagMapId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("problemId")
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private ProblemTag tag;
}
