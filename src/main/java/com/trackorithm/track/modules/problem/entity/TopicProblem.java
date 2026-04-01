package com.trackorithm.track.modules.problem.entity;

import com.trackorithm.track.modules.topic.entity.Topic;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "topic_problems",
        indexes = @Index(name = "idx_topic_problems_problem", columnList = "problem_id"))
@Getter
@Setter
public class TopicProblem {

    @EmbeddedId
    private TopicProblemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("topicId")
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("problemId")
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}

