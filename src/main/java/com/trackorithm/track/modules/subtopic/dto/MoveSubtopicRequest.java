package com.trackorithm.track.modules.subtopic.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MoveSubtopicRequest(
        @NotNull UUID targetTopicId
) {
}

