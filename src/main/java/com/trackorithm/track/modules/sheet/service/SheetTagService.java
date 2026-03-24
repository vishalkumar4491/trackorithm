package com.trackorithm.track.modules.sheet.service;

import com.trackorithm.track.modules.sheet.dto.CreateSheetTagRequest;
import com.trackorithm.track.modules.sheet.dto.SheetTagDto;

import java.util.List;
import java.util.UUID;

public interface SheetTagService {
    List<SheetTagDto> listVisible(UUID userId);

    SheetTagDto createUserTag(UUID userId, CreateSheetTagRequest request);

    SheetTagDto createSystemTag(UUID adminUserId, CreateSheetTagRequest request);
}

