package com.trackorithm.track.modules.problem.dto;

import com.trackorithm.track.common.enums.Difficulty;
import com.trackorithm.track.common.enums.Platform;

import java.util.UUID;

public record ProblemCardDto(
        UUID id,
        String title,
        String slug,
        Platform platform,
        Difficulty difficulty,
        String primaryUrl,
        String status,
        boolean bookmarked
) {
}

