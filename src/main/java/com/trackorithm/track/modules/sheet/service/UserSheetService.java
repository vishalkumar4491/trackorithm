package com.trackorithm.track.modules.sheet.service;

import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.dto.CreateUserSheetRequest;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.dto.UpdateUserSheetRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserSheetService {
    SheetSummaryDto create(UUID userId, CreateUserSheetRequest request);

    SheetSummaryDto update(UUID userId, UUID sheetId, UpdateUserSheetRequest request);

    void delete(UUID userId, UUID sheetId);

    PageResponse<SheetSummaryDto> listMyPersoanlSheets(UUID userId, Pageable pageable);
}

