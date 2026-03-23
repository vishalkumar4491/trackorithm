package com.trackorithm.track.modules.sheet.dto;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.enums.Visibility;

import java.util.UUID;

public record SheetSummaryDto(
        UUID id,
        String name,
        String description,
        SheetType type,
        Visibility visibility
) {
}

