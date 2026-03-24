package com.trackorithm.track.modules.sheet.dto;

import com.trackorithm.track.common.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateSystemSheetRequest(
        @NotBlank @Size(min = 3, max = 120) String name,
        @Size(max = 2000) String description,
        @NotNull Visibility visibility,
        @Size(max = 2000) String sourceUrl,
        List<UUID> tagIds
) {
}
