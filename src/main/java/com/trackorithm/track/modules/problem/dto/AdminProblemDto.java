package com.trackorithm.track.modules.problem.dto;

import com.trackorithm.track.common.enums.Difficulty;
import com.trackorithm.track.common.enums.Platform;
import com.trackorithm.track.modules.problem.enums.ProblemState;

import java.util.List;
import java.util.UUID;

public record AdminProblemDto(
        UUID id,
        String title,
        String slug,
        Platform platform,
        Difficulty difficulty,
        ProblemState state,
        boolean listed,
        List<ProblemLinkDto> links
) {
}

