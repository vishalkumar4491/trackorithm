package com.trackorithm.track.modules.problem.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "problem_tags",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
public class ProblemTag extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;
}
