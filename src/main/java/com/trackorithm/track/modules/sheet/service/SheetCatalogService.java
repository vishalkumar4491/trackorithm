package com.trackorithm.track.modules.sheet.service;

import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SheetCatalogService {
    PageResponse<SheetSummaryDto> listSystemSheets(Pageable pageable);

    PageResponse<SheetSummaryDto> listMyEnrolledSystemSheets(UUID userId, Pageable pageable);

    SheetSummaryDto getSystemSheet(UUID sheetId);
}

