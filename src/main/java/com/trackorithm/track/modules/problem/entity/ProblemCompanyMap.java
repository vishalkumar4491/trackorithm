package com.trackorithm.track.modules.problem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "problem_company_map")
@Getter
@Setter
public class ProblemCompanyMap {

    @EmbeddedId
    private ProblemCompanyMapId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("problemId")
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("companyId")
    @JoinColumn(name = "company_id")
    private CompanyTag company;
}
