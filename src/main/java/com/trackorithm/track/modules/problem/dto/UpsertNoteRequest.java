package com.trackorithm.track.modules.problem.dto;

import jakarta.validation.constraints.Size;

public record UpsertNoteRequest(
        @Size(max = 20000) String content
) {
}

