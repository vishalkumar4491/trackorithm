package com.trackorithm.track.modules.problem.dto;

import jakarta.validation.constraints.NotNull;

public record SetBookmarkRequest(
        @NotNull Boolean bookmarked
) {
}

