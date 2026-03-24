package com.trackorithm.track.modules.sheet.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sheet_tags")
@Getter
@Setter
public class SheetTag extends BaseEntity {

    @Column(nullable = false, length = 80)
    private String name;

    @Column(name = "is_system", nullable = false)
    private boolean system;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}

