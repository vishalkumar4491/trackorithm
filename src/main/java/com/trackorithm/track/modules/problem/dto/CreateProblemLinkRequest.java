package com.trackorithm.track.modules.problem.dto;

import com.trackorithm.track.common.enums.Difficulty;
import com.trackorithm.track.common.enums.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProblemLinkRequest(
        @NotNull Platform platform,
        @NotBlank @Size(max = 2000) String canonicalUrl,
        @Size(max = 150) String externalId,
        @Size(max = 255) String titleOnPlatform,
        Difficulty difficultyOnPlatform
) {
}

