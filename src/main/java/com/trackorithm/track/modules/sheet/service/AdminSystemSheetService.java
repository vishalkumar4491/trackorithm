package com.trackorithm.track.modules.sheet.service;

import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.dto.CreateSystemSheetRequest;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.dto.UpdateSystemSheetRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminSystemSheetService {
    SheetSummaryDto create(UUID adminUserId, CreateSystemSheetRequest request);

    SheetSummaryDto update(UUID adminUserId, UUID sheetId, UpdateSystemSheetRequest request);

    void delete(UUID sheetId);

    PageResponse<SheetSummaryDto> listAll(Pageable pageable);
}

