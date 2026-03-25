package com.trackorithm.track.modules.sheet.dto;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AddSheetTagRequest(
        UUID tagId,
        @Size(min = 2, max = 80) String name
) {
}

