package com.trackorithm.track.modules.problem.dto;

import com.trackorithm.track.common.enums.Difficulty;
import com.trackorithm.track.common.enums.Platform;

import java.util.UUID;

public record ProblemSummaryDto(
        UUID id,
        String title,
        String slug,
        Platform platform,
        Difficulty difficulty,
        String primaryUrl
) {
}

