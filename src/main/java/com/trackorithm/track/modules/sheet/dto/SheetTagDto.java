package com.trackorithm.track.modules.sheet.dto;

import java.util.UUID;

public record SheetTagDto(
        UUID id,
        String name,
        boolean system
) {
}

