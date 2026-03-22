package com.trackorithm.track.modules.problem.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.common.enums.Difficulty;
import com.trackorithm.track.common.enums.Platform;
import com.trackorithm.track.modules.sheet.entity.Topic;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "problems",
        indexes = {
                @Index(name = "idx_problems_topic_order", columnList = "topic_id, order_index"),
                @Index(name = "idx_problems_difficulty", columnList = "difficulty")
        })
@Getter
@Setter
public class Problem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    private String title;

    @Column(unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private String externalProblemId;

    private String problemUrl;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private Integer orderIndex;

    private Integer frequencyScore;

    private Double acceptanceRate;

}
