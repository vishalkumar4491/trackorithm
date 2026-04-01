package com.trackorithm.track.modules.problem.dto;

import com.trackorithm.track.common.enums.Difficulty;
import com.trackorithm.track.common.enums.Platform;

import java.util.UUID;

public record ProblemLinkDto(
        UUID id,
        Platform platform,
        String externalId,
        String canonicalUrl,
        String titleOnPlatform,
        Difficulty difficultyOnPlatform
) {
}

