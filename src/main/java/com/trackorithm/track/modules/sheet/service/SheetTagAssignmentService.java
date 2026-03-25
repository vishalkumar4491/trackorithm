package com.trackorithm.track.modules.sheet.service;

import com.trackorithm.track.modules.sheet.dto.AddSheetTagRequest;
import com.trackorithm.track.modules.sheet.dto.SheetTagDto;

import java.util.List;
import java.util.UUID;

public interface SheetTagAssignmentService {
    List<SheetTagDto> listSheetTags(UUID requesterUserId, boolean isAdmin, UUID sheetId);

    List<SheetTagDto> addTagToSheet(UUID requesterUserId, boolean isAdmin, UUID sheetId, AddSheetTagRequest request);

    List<SheetTagDto> removeTagFromSheet(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID tagId);
}

