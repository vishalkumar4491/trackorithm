package com.trackorithm.track.modules.sheet.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "topics",
        indexes = @Index(name = "idx_topics_sheet_order", columnList = "sheet_id, order_index"))
@Getter
@Setter
public class Topic extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheet_id", nullable = false)
    private Sheet sheet;

    private String name;

    @Column(name = "order_index")
    private Integer orderIndex;
}
