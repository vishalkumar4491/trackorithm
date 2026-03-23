package com.trackorithm.track.modules.sheet.mapper;

import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.entity.Sheet;

public final class SheetMapper {
    private SheetMapper() {
    }

    public static SheetSummaryDto toSummary(Sheet sheet) {
        return new SheetSummaryDto(
                sheet.getId(),
                sheet.getName(),
                sheet.getDescription(),
                sheet.getType(),
                sheet.getVisibility()
        );
    }
}

