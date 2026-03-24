package com.trackorithm.track.modules.sheet.service.impl;

import com.trackorithm.track.common.exception.ConflictException;
import com.trackorithm.track.modules.sheet.dto.CreateSheetTagRequest;
import com.trackorithm.track.modules.sheet.dto.SheetTagDto;
import com.trackorithm.track.modules.sheet.entity.SheetTag;
import com.trackorithm.track.modules.sheet.mapper.SheetTagMapper;
import com.trackorithm.track.modules.sheet.repo.SheetTagRepository;
import com.trackorithm.track.modules.sheet.service.SheetTagService;
import com.trackorithm.track.modules.user.entity.User;
import com.trackorithm.track.modules.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SheetTagServiceImpl implements SheetTagService {
    private final SheetTagRepository sheetTagRepository;
    private final UserRepository userRepository;

    public SheetTagServiceImpl(SheetTagRepository sheetTagRepository, UserRepository userRepository) {
        this.sheetTagRepository = sheetTagRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SheetTagDto> listVisible(UUID userId) {
        return sheetTagRepository.findVisibleToUser(userId).stream().map(SheetTagMapper::toDto).toList();
    }

    @Override
    @Transactional
    public SheetTagDto createUserTag(UUID userId, CreateSheetTagRequest request) {
        String name = normalize(request.name());
        if (sheetTagRepository.existsBySystemFalseAndCreatedBy_IdAndNameIgnoreCase(userId, name)) {
            throw new ConflictException("Tag already exists");
        }

        User owner = userRepository.getReferenceById(userId);
        SheetTag tag = new SheetTag();
        tag.setName(name);
        tag.setSystem(false);
        tag.setCreatedBy(owner);

        sheetTagRepository.save(tag);
        return SheetTagMapper.toDto(tag);
    }

    @Override
    @Transactional
    public SheetTagDto createSystemTag(UUID adminUserId, CreateSheetTagRequest request) {
        String name = normalize(request.name());
        if (sheetTagRepository.existsBySystemTrueAndNameIgnoreCase(name)) {
            throw new ConflictException("Tag already exists");
        }

        SheetTag tag = new SheetTag();
        tag.setName(name);
        tag.setSystem(true);
        tag.setCreatedBy(null);
        sheetTagRepository.save(tag);
        return SheetTagMapper.toDto(tag);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}

