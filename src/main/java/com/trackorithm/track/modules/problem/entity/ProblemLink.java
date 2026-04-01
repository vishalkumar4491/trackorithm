package com.trackorithm.track.modules.problem.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import com.trackorithm.track.common.enums.Difficulty;
import com.trackorithm.track.common.enums.Platform;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "problem_links",
        indexes = {
                @Index(name = "idx_problem_links_problem", columnList = "problem_id")
        })
@Getter
@Setter
public class ProblemLink extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "platform", columnDefinition = "platform_enum", nullable = false)
    private Platform platform;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "canonical_url", nullable = false)
    private String canonicalUrl;

    @Column(name = "title_on_platform")
    private String titleOnPlatform;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "difficulty_on_platform", columnDefinition = "difficulty_enum")
    private Difficulty difficultyOnPlatform;
}

