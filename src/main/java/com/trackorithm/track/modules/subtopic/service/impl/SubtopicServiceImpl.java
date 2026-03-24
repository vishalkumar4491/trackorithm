package com.trackorithm.track.modules.subtopic.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.enums.Visibility;
import com.trackorithm.track.common.exception.ConflictException;
import com.trackorithm.track.common.exception.ForbiddenException;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import com.trackorithm.track.modules.sheet.repo.SheetRepository;
import com.trackorithm.track.modules.subtopic.dto.*;
import com.trackorithm.track.modules.subtopic.entity.Subtopic;
import com.trackorithm.track.modules.subtopic.mapper.SubtopicMapper;
import com.trackorithm.track.modules.subtopic.repo.SubtopicRepository;
import com.trackorithm.track.modules.subtopic.service.SubtopicService;
import com.trackorithm.track.modules.topic.entity.Topic;
import com.trackorithm.track.modules.topic.repo.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SubtopicServiceImpl implements SubtopicService {
    private final SubtopicRepository subtopicRepository;
    private final TopicRepository topicRepository;
    private final SheetRepository sheetRepository;

    public SubtopicServiceImpl(SubtopicRepository subtopicRepository,
                               TopicRepository topicRepository,
                               SheetRepository sheetRepository) {
        this.subtopicRepository = subtopicRepository;
        this.topicRepository = topicRepository;
        this.sheetRepository = sheetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SubtopicDto> list(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, Pageable pageable) {
        requireReadableSheet(requesterUserId, isAdmin, sheetId);
        requireTopicInSheet(topicId, sheetId);
        Page<SubtopicDto> page = subtopicRepository.findByTopic_IdOrderByOrderIndexAsc(topicId, pageable)
                .map(SubtopicMapper::toDto);
        return PageResponse.from(page);
    }

    @Override
    @Transactional
    public SubtopicDto create(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, CreateSubtopicRequest request) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);
        Topic topic = requireTopicInSheet(topicId, sheetId);

        String name = normalize(request.name());
        if (subtopicRepository.existsByTopic_IdAndNameIgnoreCase(topicId, name)) {
            throw new ConflictException("Subtopic already exists");
        }

        int max = subtopicRepository.findMaxOrderIndexByTopicId(topicId);
        Subtopic s = new Subtopic();
        s.setTopic(topic);
        s.setName(name);
        s.setOrderIndex(max + 1);

        subtopicRepository.save(s);
        return SubtopicMapper.toDto(s);
    }

    @Override
    @Transactional
    public SubtopicDto update(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UUID subtopicId, UpdateSubtopicRequest request) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);
        requireTopicInSheet(topicId, sheetId);

        Subtopic s = subtopicRepository.findByIdAndTopic_Id(subtopicId, topicId)
                .orElseThrow(() -> new NotFoundException("Subtopic not found"));

        String name = normalize(request.name());
        if (!equalsIgnoreCase(s.getName(), name) && subtopicRepository.existsByTopic_IdAndNameIgnoreCase(topicId, name)) {
            throw new ConflictException("Subtopic already exists");
        }
        s.setName(name);
        subtopicRepository.save(s);
        return SubtopicMapper.toDto(s);
    }

    @Override
    @Transactional
    public void delete(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UUID subtopicId) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);
        requireTopicInSheet(topicId, sheetId);

        Subtopic s = subtopicRepository.findByIdAndTopic_Id(subtopicId, topicId)
                .orElseThrow(() -> new NotFoundException("Subtopic not found"));
        subtopicRepository.delete(s);
    }

    @Override
    @Transactional
    public List<SubtopicDto> reorder(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, ReorderSubtopicsRequest request) {
        Sheet sheet = requireWritableSheet(requesterUserId, isAdmin, sheetId);
        if (sheet.getType() == SheetType.SYSTEM) {
            if(!isAdmin) throw new ForbiddenException("Subtopic reordering is only allowed for personal sheets");
        }
        requireTopicInSheet(topicId, sheetId);

        List<UUID> orderedIds = request.orderedSubtopicIds();
        if (orderedIds.size() != new HashSet<>(orderedIds).size()) {
            throw new ConflictException("Duplicate subtopic ids in reorder request");
        }

        List<Subtopic> existing = subtopicRepository.findByTopic_IdOrderByOrderIndexAsc(topicId);
        if (existing.size() != orderedIds.size()) {
            throw new ConflictException("Reorder request must include all subtopics in the topic");
        }

        Map<UUID, Subtopic> byId = new HashMap<>();
        for (Subtopic s : existing) {
            byId.put(s.getId(), s);
        }
        for (UUID id : orderedIds) {
            if (!byId.containsKey(id)) {
                throw new ConflictException("Reorder request contains subtopic not in topic");
            }
        }

        int tmp = -1;
        List<Subtopic> inOrder = new ArrayList<>(orderedIds.size());
        for (UUID id : orderedIds) {
            Subtopic s = byId.get(id);
            s.setOrderIndex(tmp--);
            inOrder.add(s);
        }
        subtopicRepository.saveAll(inOrder);
        subtopicRepository.flush();

        for (int i = 0; i < inOrder.size(); i++) {
            inOrder.get(i).setOrderIndex(i);
        }
        subtopicRepository.saveAll(inOrder);
        subtopicRepository.flush();

        return inOrder.stream().map(SubtopicMapper::toDto).toList();
    }

    @Override
    @Transactional
    public SubtopicDto move(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID subtopicId, MoveSubtopicRequest request) {
        Sheet sheet = requireWritableSheet(requesterUserId, isAdmin, sheetId);
        if (sheet.getType() == SheetType.SYSTEM) {
            if(!isAdmin) throw new ForbiddenException("Subtopic moving is only allowed for personal sheets");
        }

        Subtopic s = subtopicRepository.findById(subtopicId)
                .orElseThrow(() -> new NotFoundException("Subtopic not found"));
        UUID currentTopicId = s.getTopic().getId();

        Topic targetTopic = requireTopicInSheet(request.targetTopicId(), sheetId);

        if (currentTopicId.equals(targetTopic.getId())) {
            return SubtopicMapper.toDto(s);
        }

        // prevent duplicates by name in target topic
        if (subtopicRepository.existsByTopic_IdAndNameIgnoreCase(targetTopic.getId(), s.getName())) {
            throw new ConflictException("Subtopic already exists in target topic");
        }

        int max = subtopicRepository.findMaxOrderIndexByTopicId(targetTopic.getId());
        s.setTopic(targetTopic);
        s.setOrderIndex(max + 1);
        subtopicRepository.save(s);
        return SubtopicMapper.toDto(s);
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

    private Topic requireTopicInSheet(UUID topicId, UUID sheetId) {
        return topicRepository.findByIdAndSheet_Id(topicId, sheetId)
                .orElseThrow(() -> new NotFoundException("Topic not found"));
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
