package com.trackorithm.track.modules.subtopic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSubtopicRequest(
        @NotBlank @Size(min = 1, max = 255) String name
) {
}

