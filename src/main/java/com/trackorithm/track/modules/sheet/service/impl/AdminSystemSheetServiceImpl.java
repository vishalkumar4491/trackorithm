package com.trackorithm.track.modules.sheet.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.dto.CreateSystemSheetRequest;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.dto.UpdateSystemSheetRequest;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import com.trackorithm.track.modules.sheet.mapper.SheetMapper;
import com.trackorithm.track.modules.sheet.repo.SheetRepository;
import com.trackorithm.track.modules.sheet.service.AdminSystemSheetService;
import com.trackorithm.track.modules.user.entity.User;
import com.trackorithm.track.modules.user.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminSystemSheetServiceImpl implements AdminSystemSheetService {
    private final SheetRepository sheetRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SheetSummaryDto create(UUID adminUserId, CreateSystemSheetRequest request) {
        User adminRef = userRepository.getReferenceById(adminUserId);

        Sheet sheet = new Sheet();
        sheet.setName(request.name());
        sheet.setDescription(blankToNull(request.description()));
        sheet.setType(SheetType.SYSTEM);
        sheet.setVisibility(request.visibility());
        sheet.setCreatedBy(adminRef);

        sheetRepository.save(sheet);
        return SheetMapper.toSummary(sheet);
    }

    @Override
    @Transactional
    public SheetSummaryDto update(UUID adminUserId, UUID sheetId, UpdateSystemSheetRequest request) {
        Sheet sheet = sheetRepository.findByIdAndType(sheetId, SheetType.SYSTEM)
                .orElseThrow(() -> new NotFoundException("System sheet not found"));

        if (request.name() != null && !request.name().isBlank()) {
            sheet.setName(request.name());
        }
        if (request.description() != null) {
            sheet.setDescription(blankToNull(request.description()));
        }
        if (request.visibility() != null) {
            sheet.setVisibility(request.visibility());
        }

        sheetRepository.save(sheet);
        return SheetMapper.toSummary(sheet);
    }

    @Override
    @Transactional
    public void delete(UUID sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new NotFoundException("Sheet not found"));
        sheetRepository.delete(sheet);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SheetSummaryDto> listAll(Pageable pageable) {
        Page<SheetSummaryDto> page = sheetRepository.findByType(SheetType.SYSTEM, pageable)
                .map(SheetMapper::toSummary);
        return PageResponse.from(page);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

