package com.trackorithm.track.modules.problem.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company_tags",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
public class CompanyTag extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;
}
