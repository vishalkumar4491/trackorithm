package com.trackorithm.track.modules.problem.dto;

import com.trackorithm.track.common.enums.Difficulty;
import com.trackorithm.track.common.enums.Platform;
import com.trackorithm.track.modules.problem.enums.ProblemState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProblemRequest(
        @NotNull Platform platform,
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 255) String slug,
        @NotBlank @Size(max = 2000) String canonicalUrl,
        @Size(max = 150) String externalId,
        Difficulty difficulty,
        ProblemState state,
        Boolean listed
) {
}

