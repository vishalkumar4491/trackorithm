package com.trackorithm.track.modules.problem.dto;

import com.trackorithm.track.modules.problem.dto.ProblemEditorialDto;

import java.util.List;

public record ProblemDetailsDto(
        ProblemSummaryDto problem,
        List<ProblemLinkDto> links,
        ProblemEditorialDto editorial,
        boolean bookmarked,
        String status,
        String note
) {
}

