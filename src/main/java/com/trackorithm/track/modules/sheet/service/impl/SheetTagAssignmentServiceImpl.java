package com.trackorithm.track.modules.sheet.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.enums.Visibility;
import com.trackorithm.track.common.exception.ConflictException;
import com.trackorithm.track.common.exception.ForbiddenException;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.modules.sheet.dto.AddSheetTagRequest;
import com.trackorithm.track.modules.sheet.dto.SheetTagDto;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import com.trackorithm.track.modules.sheet.entity.SheetTag;
import com.trackorithm.track.modules.sheet.entity.SheetTagMap;
import com.trackorithm.track.modules.sheet.entity.SheetTagMapId;
import com.trackorithm.track.modules.sheet.mapper.SheetTagMapper;
import com.trackorithm.track.modules.sheet.repo.SheetRepository;
import com.trackorithm.track.modules.sheet.repo.SheetTagMapRepository;
import com.trackorithm.track.modules.sheet.repo.SheetTagRepository;
import com.trackorithm.track.modules.sheet.service.SheetTagAssignmentService;
import com.trackorithm.track.modules.user.entity.User;
import com.trackorithm.track.modules.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SheetTagAssignmentServiceImpl implements SheetTagAssignmentService {
    private final SheetRepository sheetRepository;
    private final SheetTagRepository sheetTagRepository;
    private final SheetTagMapRepository sheetTagMapRepository;
    private final UserRepository userRepository;

    public SheetTagAssignmentServiceImpl(SheetRepository sheetRepository,
                                        SheetTagRepository sheetTagRepository,
                                        SheetTagMapRepository sheetTagMapRepository,
                                        UserRepository userRepository) {
        this.sheetRepository = sheetRepository;
        this.sheetTagRepository = sheetTagRepository;
        this.sheetTagMapRepository = sheetTagMapRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SheetTagDto> listSheetTags(UUID requesterUserId, boolean isAdmin, UUID sheetId) {
        requireReadableSheet(requesterUserId, isAdmin, sheetId);
        return sheetTagMapRepository.findTagsBySheetId(sheetId).stream().map(SheetTagMapper::toDto).toList();
    }

    @Override
    @Transactional
    public List<SheetTagDto> addTagToSheet(UUID requesterUserId, boolean isAdmin, UUID sheetId, AddSheetTagRequest request) {
        Sheet sheet = requireWritableSheet(requesterUserId, isAdmin, sheetId);

        if (request.tagId() == null && (request.name() == null || request.name().isBlank())) {
            throw new IllegalArgumentException("Either tagId or name is required");
        }

        SheetTag tag;
        if (request.tagId() != null) {
            tag = sheetTagRepository.findById(request.tagId()).orElseThrow(() -> new NotFoundException("Tag not found"));
        } else {
            String normalized = request.name().trim();
            tag = sheetTagRepository.findFirstByNameIgnoreCase(normalized).orElseGet(() -> {
                SheetTag t = new SheetTag();
                t.setName(normalized);
                t.setSystem(sheet.getType() == SheetType.SYSTEM);
                if (!t.isSystem()) {
                    User creator = userRepository.getReferenceById(requesterUserId);
                    t.setCreatedBy(creator);
                }
                return sheetTagRepository.save(t);
            });

            // If admin attaches to a system sheet, promote the tag to system.
            if (sheet.getType() == SheetType.SYSTEM && !tag.isSystem()) {
                tag.setSystem(true);
                sheetTagRepository.save(tag);
            }
        }

        if (sheet.getType() == SheetType.SYSTEM && !tag.isSystem()) {
            throw new ConflictException("System sheets can only use system tags");
        }

        if (sheetTagMapRepository.existsBySheet_IdAndTag_Id(sheetId, tag.getId())) {
            throw new ConflictException("Tag already exists for this sheet");
        }

        SheetTagMap map = new SheetTagMap();
        map.setId(new SheetTagMapId(sheetId, tag.getId()));
        map.setSheet(sheet);
        map.setTag(tag);
        sheetTagMapRepository.save(map);

        return sheetTagMapRepository.findTagsBySheetId(sheetId).stream().map(SheetTagMapper::toDto).toList();
    }

    @Override
    @Transactional
    public List<SheetTagDto> removeTagFromSheet(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID tagId) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);
        SheetTagMapId id = new SheetTagMapId(sheetId, tagId);
        if (sheetTagMapRepository.existsById(id)) {
            sheetTagMapRepository.deleteById(id);
        }
        else{
            throw new IllegalArgumentException("Tag not linked with sheet");
        }
        return sheetTagMapRepository.findTagsBySheetId(sheetId).stream().map(SheetTagMapper::toDto).toList();
    }

    private Sheet requireReadableSheet(UUID requesterUserId, boolean isAdmin, UUID sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId).orElseThrow(() -> new NotFoundException("Sheet not found"));
        if (sheet.getType() == SheetType.SYSTEM) {
            if (sheet.getVisibility() == Visibility.PUBLIC || isAdmin) {
                return sheet;
            }
            throw new ForbiddenException("Sheet is not accessible");
        }
        if (sheet.getCreatedBy() != null && requesterUserId.equals(sheet.getCreatedBy().getId())) {
            return sheet;
        }
        throw new ForbiddenException("Sheet is not accessible");
    }

    private Sheet requireWritableSheet(UUID requesterUserId, boolean isAdmin, UUID sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId).orElseThrow(() -> new NotFoundException("Sheet not found"));
        if (sheet.getType() == SheetType.SYSTEM) {
            if (isAdmin) {
                return sheet;
            }
            throw new ForbiddenException("Only admin can modify system sheets");
        }
        if (sheet.getCreatedBy() != null && requesterUserId.equals(sheet.getCreatedBy().getId())) {
            return sheet;
        }
        throw new ForbiddenException("Only owner can modify this sheet");
    }
}
