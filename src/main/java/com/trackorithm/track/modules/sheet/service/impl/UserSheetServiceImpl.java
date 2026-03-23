package com.trackorithm.track.modules.sheet.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.enums.Visibility;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.dto.CreateUserSheetRequest;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.dto.UpdateUserSheetRequest;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import com.trackorithm.track.modules.sheet.mapper.SheetMapper;
import com.trackorithm.track.modules.sheet.repo.SheetRepository;
import com.trackorithm.track.modules.sheet.service.UserSheetService;
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
public class UserSheetServiceImpl implements UserSheetService {
    private final SheetRepository sheetRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SheetSummaryDto create(UUID userId, CreateUserSheetRequest request) {
        User owner = userRepository.getReferenceById(userId);

        Sheet sheet = new Sheet();
        sheet.setName(request.name());
        sheet.setDescription(blankToNull(request.description()));
        sheet.setType(SheetType.USER);
        sheet.setVisibility(Visibility.PRIVATE);
        sheet.setCreatedBy(owner);

        sheetRepository.save(sheet);
        return SheetMapper.toSummary(sheet);
    }

    @Override
    @Transactional
    public SheetSummaryDto update(UUID userId, UUID sheetId, UpdateUserSheetRequest request) {
        Sheet sheet = sheetRepository.findOwnedUserSheet(sheetId, SheetType.USER, userId)
                .orElseThrow(() -> new NotFoundException("User sheet not found"));

        if (request.name() != null && !request.name().isBlank()) {
            sheet.setName(request.name());
        }
        if (request.description() != null) {
            sheet.setDescription(blankToNull(request.description()));
        }

        sheetRepository.save(sheet);
        return SheetMapper.toSummary(sheet);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID sheetId) {
        Sheet sheet = sheetRepository.findOwnedUserSheet(sheetId, SheetType.USER, userId)
                .orElseThrow(() -> new NotFoundException("User sheet not found"));
        sheetRepository.delete(sheet);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SheetSummaryDto> listMyPersoanlSheets(UUID userId, Pageable pageable) {
        Page<SheetSummaryDto> page = sheetRepository
                .findByTypeAndCreatedById(SheetType.USER, userId, pageable)
                .map(SheetMapper::toSummary);
        return PageResponse.from(page);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
