package com.trackorithm.track.modules.problem.dto;

import java.util.UUID;

public record ProblemEditorialDto(
        UUID problemId,
        String content,
        String youtubeUrl,
        String referenceUrl
) {
}

