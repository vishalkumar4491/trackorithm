package com.trackorithm.track.modules.problem.service;

import com.trackorithm.track.modules.problem.dto.ProblemEditorialDto;
import com.trackorithm.track.modules.problem.dto.UpsertProblemEditorialRequest;

import java.util.Optional;
import java.util.UUID;

public interface ProblemEditorialService {
    Optional<ProblemEditorialDto> get(UUID problemId);

    Optional<ProblemEditorialDto> upsert(UUID adminUserId, UUID problemId, UpsertProblemEditorialRequest request);
}

