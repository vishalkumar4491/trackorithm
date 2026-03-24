package com.trackorithm.track.modules.subtopic.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.modules.topic.entity.Topic;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subtopics",
        indexes = @Index(name = "idx_subtopics_topic_order", columnList = "topic_id, order_index"))
@Getter
@Setter
public class Subtopic extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}
