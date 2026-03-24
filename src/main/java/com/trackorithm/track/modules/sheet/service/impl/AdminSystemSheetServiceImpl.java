package com.trackorithm.track.modules.sheet.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.exception.ConflictException;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.dto.CreateSystemSheetRequest;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.dto.UpdateSystemSheetRequest;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import com.trackorithm.track.modules.sheet.entity.SheetTag;
import com.trackorithm.track.modules.sheet.entity.SheetTagMap;
import com.trackorithm.track.modules.sheet.entity.SheetTagMapId;
import com.trackorithm.track.modules.sheet.mapper.SheetMapper;
import com.trackorithm.track.modules.sheet.repo.SheetRepository;
import com.trackorithm.track.modules.sheet.repo.SheetTagMapRepository;
import com.trackorithm.track.modules.sheet.repo.SheetTagRepository;
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
    private final SheetTagRepository sheetTagRepository;
    private final SheetTagMapRepository sheetTagMapRepository;

    @Override
    @Transactional
    public SheetSummaryDto create(UUID adminUserId, CreateSystemSheetRequest request) {
        if (sheetRepository.existsByTypeAndNameIgnoreCase(SheetType.SYSTEM, request.name())) {
            throw new ConflictException("System sheet name already exists");
        }

        User adminRef = userRepository.getReferenceById(adminUserId);

        Sheet sheet = new Sheet();
        sheet.setName(request.name());
        sheet.setDescription(blankToNull(request.description()));
        sheet.setSourceUrl(blankToNull(request.sourceUrl()));
        sheet.setType(SheetType.SYSTEM);
        sheet.setVisibility(request.visibility());
        sheet.setCreatedBy(adminRef);

        sheetRepository.save(sheet);
        replaceSystemTags(sheet, request.tagIds());
        return SheetMapper.toSummary(sheet);
    }

    @Override
    @Transactional
    public SheetSummaryDto update(UUID adminUserId, UUID sheetId, UpdateSystemSheetRequest request) {
        Sheet sheet = sheetRepository.findByIdAndType(sheetId, SheetType.SYSTEM)
                .orElseThrow(() -> new NotFoundException("System sheet not found"));

        if (request.name() != null && !request.name().isBlank()) {
            if (sheetRepository.existsOtherSystemSheetWithName(SheetType.SYSTEM, request.name(), sheetId)) {
                throw new ConflictException("System sheet name already exists");
            }
            sheet.setName(request.name());
        }
        if (request.description() != null) {
            sheet.setDescription(blankToNull(request.description()));
        }
        if (request.visibility() != null) {
            sheet.setVisibility(request.visibility());
        }
        if (request.sourceUrl() != null) {
            sheet.setSourceUrl(blankToNull(request.sourceUrl()));
        }

        sheetRepository.save(sheet);
        if (request.tagIds() != null) {
            replaceSystemTags(sheet, request.tagIds());
        }
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

    private void replaceSystemTags(Sheet sheet, java.util.List<UUID> tagIds) {
        sheetTagMapRepository.deleteBySheetId(sheet.getId());
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        java.util.List<SheetTag> tags = sheetTagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size() || tags.stream().anyMatch(t -> !t.isSystem())) {
            throw new ConflictException("Invalid sheet tags");
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
