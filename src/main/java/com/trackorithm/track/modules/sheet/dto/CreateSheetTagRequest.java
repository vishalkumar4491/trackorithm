package com.trackorithm.track.modules.sheet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSheetTagRequest(
        @NotBlank @Size(min = 2, max = 80) String name
) {
}

