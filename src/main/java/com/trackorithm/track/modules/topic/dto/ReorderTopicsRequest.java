package com.trackorithm.track.modules.topic.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderTopicsRequest(
        @NotEmpty List<UUID> orderedTopicIds
) {
}

