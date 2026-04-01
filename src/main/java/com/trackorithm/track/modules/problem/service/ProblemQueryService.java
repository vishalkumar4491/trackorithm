package com.trackorithm.track.modules.problem.service;

import com.trackorithm.track.modules.problem.dto.ProblemDetailsDto;
import com.trackorithm.track.modules.problem.dto.ProblemSummaryDto;

import java.util.List;
import java.util.UUID;

public interface ProblemQueryService {
    List<ProblemSummaryDto> search(String q);

    ProblemDetailsDto getDetails(UUID userId, UUID problemId);
}

