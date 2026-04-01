package com.trackorithm.track.modules.problem.mapper;

import com.trackorithm.track.modules.problem.dto.ProblemLinkDto;
import com.trackorithm.track.modules.problem.entity.ProblemLink;

public final class ProblemLinkMapper {
    private ProblemLinkMapper() {
    }

    public static ProblemLinkDto toDto(ProblemLink link) {
        return new ProblemLinkDto(
                link.getId(),
                link.getPlatform(),
                link.getExternalId(),
                link.getCanonicalUrl(),
                link.getTitleOnPlatform(),
                link.getDifficultyOnPlatform()
        );
    }
}

