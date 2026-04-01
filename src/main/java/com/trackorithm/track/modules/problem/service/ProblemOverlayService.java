package com.trackorithm.track.modules.problem.service;

import com.trackorithm.track.modules.problem.dto.ProblemDetailsDto;

import java.util.UUID;

public interface ProblemOverlayService {
    ProblemDetailsDto setBookmark(UUID userId, UUID problemId, boolean bookmarked);

    ProblemDetailsDto upsertNote(UUID userId, UUID problemId, String content);
}

