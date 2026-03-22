package com.trackorithm.track.modules.sheet.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.enums.Visibility;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sheets",
        indexes = @Index(name = "idx_sheets_created_by", columnList = "created_by"))
@Getter
@Setter
public class Sheet extends BaseEntity {

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private SheetType type;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}
