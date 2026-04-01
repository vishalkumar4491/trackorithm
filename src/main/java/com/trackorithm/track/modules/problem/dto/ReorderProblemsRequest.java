package com.trackorithm.track.modules.problem.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderProblemsRequest(
        @NotEmpty List<UUID> orderedProblemIds
) {
}

