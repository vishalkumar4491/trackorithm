package com.trackorithm.track.modules.subtopic.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderSubtopicsRequest(
        @NotEmpty List<UUID> orderedSubtopicIds
) {
}

