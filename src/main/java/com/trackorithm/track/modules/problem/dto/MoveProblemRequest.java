package com.trackorithm.track.modules.problem.dto;

import java.util.UUID;

public record MoveProblemRequest(
        UUID targetTopicId,
        UUID targetSubtopicId,
        Integer position
) {
}

