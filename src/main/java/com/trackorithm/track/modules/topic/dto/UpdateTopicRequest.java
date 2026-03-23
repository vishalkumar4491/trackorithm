package com.trackorithm.track.modules.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTopicRequest(
        @NotBlank @Size(min = 1, max = 255) String name
) {
}

