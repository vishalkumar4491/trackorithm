package com.trackorithm.track.modules.sheet.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.enums.Visibility;
import com.trackorithm.track.common.exception.ConflictException;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.dto.CreateUserSheetRequest;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.dto.UpdateUserSheetRequest;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import com.trackorithm.track.modules.sheet.entity.SheetTag;
import com.trackorithm.track.modules.sheet.entity.SheetTagMap;
import com.trackorithm.track.modules.sheet.entity.SheetTagMapId;
import com.trackorithm.track.modules.sheet.mapper.SheetMapper;
import com.trackorithm.track.modules.sheet.repo.SheetRepository;
import com.trackorithm.track.modules.sheet.repo.SheetTagMapRepository;
import com.trackorithm.track.modules.sheet.repo.SheetTagRepository;
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
    private final SheetTagRepository sheetTagRepository;
    private final SheetTagMapRepository sheetTagMapRepository;

    @Override
    @Transactional
    public SheetSummaryDto create(UUID userId, CreateUserSheetRequest request) {
        if (sheetRepository.existsByTypeAndCreatedBy_IdAndNameIgnoreCase(SheetType.USER, userId, request.name())) {
            throw new ConflictException("Sheet name already exists");
        }

        User owner = userRepository.getReferenceById(userId);

        Sheet sheet = new Sheet();
        sheet.setName(request.name());
        sheet.setDescription(blankToNull(request.description()));
        sheet.setType(SheetType.USER);
        sheet.setVisibility(Visibility.PRIVATE);
        sheet.setCreatedBy(owner);

        sheetRepository.save(sheet);
        replaceUserSheetTags(userId, sheet, request.tagIds());
        return SheetMapper.toSummary(sheet);
    }

    @Override
    @Transactional
    public SheetSummaryDto update(UUID userId, UUID sheetId, UpdateUserSheetRequest request) {
        Sheet sheet = sheetRepository.findOwnedUserSheet(sheetId, SheetType.USER, userId)
                .orElseThrow(() -> new NotFoundException("User sheet not found"));

        if (request.name() != null && !request.name().isBlank()) {
            if (sheetRepository.existsOtherOwnedSheetWithName(SheetType.USER, userId, request.name(), sheetId)) {
                throw new ConflictException("Sheet name already exists");
            }
            sheet.setName(request.name());
        }
        if (request.description() != null) {
            sheet.setDescription(blankToNull(request.description()));
        }

        sheetRepository.save(sheet);
        if (request.tagIds() != null) {
            replaceUserSheetTags(userId, sheet, request.tagIds());
        }
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

    private void replaceUserSheetTags(UUID userId, Sheet sheet, java.util.List<UUID> tagIds) {
        sheetTagMapRepository.deleteBySheetId(sheet.getId());
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        java.util.List<SheetTag> tags = sheetTagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
            throw new ConflictException("Invalid sheet tags");
        }
        for (SheetTag t : tags) {
            boolean ok = t.isSystem() || (t.getCreatedBy() != null && userId.equals(t.getCreatedBy().getId()));
            if (!ok) {
                throw new ConflictException("Invalid sheet tags");
            }
        }

        java.util.List<SheetTagMap> maps = new java.util.ArrayList<>(tags.size());
        for (SheetTag t : tags) {
            SheetTagMap m = new SheetTagMap();
            m.setId(new SheetTagMapId(sheet.getId(), t.getId()));
            m.setSheet(sheet);
            m.setTag(t);
            maps.add(m);
        }
        sheetTagMapRepository.saveAll(maps);
    }
}
