package com.trackorithm.track.modules.problem.mapper;

import com.trackorithm.track.modules.problem.dto.ProblemEditorialDto;
import com.trackorithm.track.modules.problem.entity.ProblemEditorial;

public final class ProblemEditorialMapper {
    private ProblemEditorialMapper() {
    }

    public static ProblemEditorialDto toDto(ProblemEditorial editorial) {
        return new ProblemEditorialDto(
                editorial.getProblem().getId(),
                editorial.getContent(),
                editorial.getYoutubeUrl(),
                editorial.getReferenceUrl()
        );
    }
}

