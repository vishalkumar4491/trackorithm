package com.trackorithm.track.modules.problem.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.common.enums.Difficulty;
import com.trackorithm.track.common.enums.Platform;
import com.trackorithm.track.modules.topic.entity.Topic;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "problems",
        indexes = {
                @Index(name = "idx_problems_topic_order", columnList = "topic_id, order_index"),
                @Index(name = "idx_problems_difficulty", columnList = "difficulty")
        })
@Getter
@Setter
public class Problem extends BaseEntity {

    private String title;

    @Column(unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "platform", columnDefinition = "platform_enum")
    private Platform platform;

    private String externalProblemId;

    private String problemUrl;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "difficulty", columnDefinition = "difficulty_enum")
    private Difficulty difficulty;

    private Integer frequencyScore;

    private Double acceptanceRate;

}
