package com.trackorithm.track.modules.problem.dto;

import jakarta.validation.constraints.Size;

public record UpsertProblemEditorialRequest(
        @Size(max = 20000) String content,
        @Size(max = 2000) String youtubeUrl,
        @Size(max = 2000) String referenceUrl
) {
}

