package com.trackorithm.track.modules.tag.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_tags",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
@Getter
@Setter
public class UserTag extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String name;
}
