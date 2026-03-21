package com.trackorithm.track.modules.tracking.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_problem_status")
@Getter
@Setter
public class UserProblemStatus extends BaseEntity {
}
