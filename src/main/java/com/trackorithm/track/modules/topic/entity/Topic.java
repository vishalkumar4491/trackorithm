package com.trackorithm.track.modules.topic.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "topics",
        indexes = {
                @Index(name = "ux_topics_sheet_order_index", columnList = "sheet_id, order_index", unique = true),
                @Index(name = "ux_topics_sheet_name_lower", columnList = "sheet_id, lower(name)", unique = true)
        })
@Getter
@Setter
public class Topic extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheet_id", nullable = false)
    private Sheet sheet;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}
