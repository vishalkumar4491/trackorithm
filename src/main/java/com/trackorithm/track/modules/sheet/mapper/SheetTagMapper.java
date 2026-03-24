package com.trackorithm.track.modules.sheet.mapper;

import com.trackorithm.track.modules.sheet.dto.SheetTagDto;
import com.trackorithm.track.modules.sheet.entity.SheetTag;

public final class SheetTagMapper {
    private SheetTagMapper() {
    }

    public static SheetTagDto toDto(SheetTag tag) {
        return new SheetTagDto(tag.getId(), tag.getName(), tag.isSystem());
    }
}

