package com.trackorithm.track.modules.topic.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.enums.Visibility;
import com.trackorithm.track.common.exception.ConflictException;
import com.trackorithm.track.common.exception.ForbiddenException;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import com.trackorithm.track.modules.sheet.repo.SheetRepository;
import com.trackorithm.track.modules.topic.dto.CreateTopicRequest;
import com.trackorithm.track.modules.topic.dto.ReorderTopicsRequest;
import com.trackorithm.track.modules.topic.dto.TopicDto;
import com.trackorithm.track.modules.topic.dto.UpdateTopicRequest;
import com.trackorithm.track.modules.topic.entity.Topic;
import com.trackorithm.track.modules.topic.mapper.TopicMapper;
import com.trackorithm.track.modules.topic.repo.TopicRepository;
import com.trackorithm.track.modules.topic.service.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;
    private final SheetRepository sheetRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TopicDto> list(UUID requesterUserId, boolean isAdmin, UUID sheetId, Pageable pageable) {
        Sheet sheet = requireReadableSheet(requesterUserId, isAdmin, sheetId);
        Page<TopicDto> page = topicRepository.findBySheet_IdOrderByOrderIndexAsc(sheet.getId(), pageable)
                .map(TopicMapper::toDto);
        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public TopicDto create(UUID requesterUserId, boolean isAdmin, UUID sheetId, CreateTopicRequest request) {
        Sheet sheet = requireWritableSheet(requesterUserId, isAdmin, sheetId);

        String normalizedName = normalize(request.name());
        if (topicRepository.existsBySheet_IdAndNameIgnoreCase(sheet.getId(), normalizedName)) {
            throw new ConflictException("Topic already exists in this sheet");
        }

        int max = topicRepository.findMaxOrderIndexBySheetId(sheet.getId());
        Topic topic = new Topic();
        topic.setSheet(sheet);
        topic.setName(normalizedName);
        topic.setOrderIndex(max + 1);

        topicRepository.save(topic);
        return TopicMapper.toDto(topic);
    }

    @Override
    @Transactional
    public TopicDto update(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UpdateTopicRequest request) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);

        Topic topic = topicRepository.findByIdAndSheet_Id(topicId, sheetId)
                .orElseThrow(() -> new NotFoundException("Topic not found"));

        String normalizedName = normalize(request.name());
        if (!equalsIgnoreCase(topic.getName(), normalizedName)
                && topicRepository.existsBySheet_IdAndNameIgnoreCase(sheetId, normalizedName)) {
            throw new ConflictException("Topic already exists in this sheet");
        }

        topic.setName(normalizedName);
        topicRepository.save(topic);
        return TopicMapper.toDto(topic);
    }

    @Override
    @Transactional
    public void delete(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);
        Topic topic = topicRepository.findByIdAndSheet_Id(topicId, sheetId)
                .orElseThrow(() -> new NotFoundException("Topic not found"));
        topicRepository.delete(topic);
    }

    @Override
    @Transactional
    public List<TopicDto> reorder(UUID requesterUserId, boolean isAdmin, UUID sheetId, ReorderTopicsRequest request) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);

        List<UUID> orderedIds = request.orderedTopicIds();
        if (orderedIds.size() != new HashSet<>(orderedIds).size()) {
            throw new ConflictException("Duplicate topic ids in reorder request");
        }

        List<Topic> existing = topicRepository.findBySheet_IdOrderByOrderIndexAsc(sheetId);
        if (existing.size() != orderedIds.size()) {
            throw new ConflictException("Reorder request must include all topics in the sheet");
        }

        Map<UUID, Topic> byId = new HashMap<>();
        for (Topic t : existing) {
            byId.put(t.getId(), t);
        }

        for (UUID id : orderedIds) {
            if (!byId.containsKey(id)) {
                throw new ConflictException("Reorder request contains topic not in sheet");
            }
        }

        // Two-phase assignment to avoid unique(sheet_id, order_index) conflicts mid-update.
        int tmp = -1;
        List<Topic> inOrder = new ArrayList<>(orderedIds.size());
        for (UUID id : orderedIds) {
            Topic t = byId.get(id);
            t.setOrderIndex(tmp--);
            inOrder.add(t);
        }
        topicRepository.saveAll(inOrder);
        topicRepository.flush();

        for (int i = 0; i < inOrder.size(); i++) {
            inOrder.get(i).setOrderIndex(i);
        }
        topicRepository.saveAll(inOrder);
        topicRepository.flush();

        return inOrder.stream().map(TopicMapper::toDto).toList();
    }

    private Sheet requireReadableSheet(UUID requesterUserId, boolean isAdmin, UUID sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId).orElseThrow(() -> new NotFoundException("Sheet not found"));

        if (sheet.getType() == SheetType.SYSTEM) {
            if (sheet.getVisibility() == Visibility.PUBLIC || isAdmin) {
                return sheet;
            }
            throw new ForbiddenException("Sheet is not accessible");
        }

        // USER sheet: only owner can read for now (future: sharing/public).
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

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private static boolean equalsIgnoreCase(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    }
}

