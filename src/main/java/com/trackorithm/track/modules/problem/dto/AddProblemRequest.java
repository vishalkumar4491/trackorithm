package com.trackorithm.track.modules.problem.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddProblemRequest(
        @NotNull UUID problemId,
        Integer position
) {
}

