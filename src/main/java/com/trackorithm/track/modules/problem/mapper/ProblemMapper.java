package com.trackorithm.track.modules.problem.mapper;

import com.trackorithm.track.modules.problem.dto.ProblemSummaryDto;
import com.trackorithm.track.modules.problem.entity.Problem;

public final class ProblemMapper {
    private ProblemMapper() {
    }

    public static ProblemSummaryDto toSummary(Problem p) {
        return new ProblemSummaryDto(
                p.getId(),
                p.getTitle(),
                p.getSlug(),
                p.getPlatform(),
                p.getDifficulty(),
                p.getProblemUrl()
        );
    }
}

