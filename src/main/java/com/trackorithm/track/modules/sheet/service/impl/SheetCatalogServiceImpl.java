package com.trackorithm.track.modules.sheet.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.enums.Visibility;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.mapper.SheetMapper;
import com.trackorithm.track.modules.sheet.repo.SheetRepository;
import com.trackorithm.track.modules.sheet.repo.UserSheetEnrollmentRepository;
import com.trackorithm.track.modules.sheet.service.SheetCatalogService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SheetCatalogServiceImpl implements SheetCatalogService {
    private final SheetRepository sheetRepository;
    private final UserSheetEnrollmentRepository enrollmentRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SheetSummaryDto> listSystemSheets(Pageable pageable) {
        Page<SheetSummaryDto> page = sheetRepository
                .findByTypeAndVisibility(SheetType.SYSTEM, Visibility.PUBLIC, pageable)
                .map(SheetMapper::toSummary);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SheetSummaryDto> listMyEnrolledSystemSheets(UUID userId, Pageable pageable) {
        Page<SheetSummaryDto> page = enrollmentRepository.findActiveSheetsByUserId(userId, pageable)
                .map(SheetMapper::toSummary);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public SheetSummaryDto getSystemSheet(UUID sheetId) {
        return sheetRepository.findByIdAndType(sheetId, SheetType.SYSTEM)
                .map(SheetMapper::toSummary)
                .orElseThrow(() -> new NotFoundException("Sheet not found"));
    }
}
