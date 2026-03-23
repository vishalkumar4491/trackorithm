package com.trackorithm.track.modules.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTopicRequest(
        @NotBlank @Size(min = 1, max = 255) String name
) {
}

