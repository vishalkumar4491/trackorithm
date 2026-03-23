package com.trackorithm.track.modules.sheet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserSheetRequest(
        @NotBlank @Size(min = 3, max = 120) String name,
        @Size(max = 2000) String description
) {
}

