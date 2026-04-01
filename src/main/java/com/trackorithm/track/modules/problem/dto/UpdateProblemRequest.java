package com.trackorithm.track.modules.problem.dto;

import com.trackorithm.track.common.enums.Difficulty;
import com.trackorithm.track.common.enums.Platform;
import com.trackorithm.track.modules.problem.enums.ProblemState;
import jakarta.validation.constraints.Size;

public record UpdateProblemRequest(
        @Size(max = 255) String title,
        @Size(max = 255) String slug,
        Platform platform,
        Difficulty difficulty,
        ProblemState state,
        Boolean listed
) {
}

