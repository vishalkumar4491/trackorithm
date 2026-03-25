package com.trackorithm.track.modules.sheet.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.enums.Visibility;
import com.trackorithm.track.common.exception.ForbiddenException;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.modules.sheet.dto.SheetHierarchyDto;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.dto.SheetTagDto;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import com.trackorithm.track.modules.sheet.mapper.SheetMapper;
import com.trackorithm.track.modules.sheet.mapper.SheetTagMapper;
import com.trackorithm.track.modules.sheet.repo.SheetRepository;
import com.trackorithm.track.modules.sheet.repo.SheetTagMapRepository;
import com.trackorithm.track.modules.sheet.service.SheetHierarchyService;
import com.trackorithm.track.modules.subtopic.dto.SubtopicDto;
import com.trackorithm.track.modules.subtopic.dto.SubtopicHierarchyDto;
import com.trackorithm.track.modules.subtopic.mapper.SubtopicMapper;
import com.trackorithm.track.modules.subtopic.repo.SubtopicRepository;
import com.trackorithm.track.modules.topic.dto.TopicDto;
import com.trackorithm.track.modules.topic.dto.TopicHierarchyDto;
import com.trackorithm.track.modules.topic.mapper.TopicMapper;
import com.trackorithm.track.modules.topic.repo.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SheetHierarchyServiceImpl implements SheetHierarchyService {
    private final SheetRepository sheetRepository;
    private final SheetTagMapRepository sheetTagMapRepository;
    private final TopicRepository topicRepository;
    private final SubtopicRepository subtopicRepository;

    public SheetHierarchyServiceImpl(SheetRepository sheetRepository,
                                    SheetTagMapRepository sheetTagMapRepository,
                                    TopicRepository topicRepository,
                                    SubtopicRepository subtopicRepository) {
        this.sheetRepository = sheetRepository;
        this.sheetTagMapRepository = sheetTagMapRepository;
        this.topicRepository = topicRepository;
        this.subtopicRepository = subtopicRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SheetHierarchyDto sheetHierarchy(UUID requesterUserId, boolean isAdmin, UUID sheetId) {
        Sheet sheet = requireReadableSheet(requesterUserId, isAdmin, sheetId);
        SheetSummaryDto sheetDto = SheetMapper.toSummary(sheet);

        List<SheetTagDto> tags = sheetTagMapRepository.findTagsBySheetId(sheetId).stream()
                .map(SheetTagMapper::toDto)
                .toList();

        List<com.trackorithm.track.modules.topic.entity.Topic> topics = topicRepository.findBySheet_IdOrderByOrderIndexAsc(sheetId);
        List<UUID> topicIds = topics.stream().map(t -> t.getId()).toList();

        Map<UUID, List<SubtopicDto>> subtopicsByTopic = new HashMap<>();
        if (!topicIds.isEmpty()) {
            subtopicRepository.findByTopic_IdInOrderByTopic_IdAscOrderIndexAsc(topicIds).forEach(s -> {
                subtopicsByTopic.computeIfAbsent(s.getTopic().getId(), k -> new ArrayList<>())
                        .add(SubtopicMapper.toDto(s));
            });
        }

        List<TopicHierarchyDto> topicTrees = new ArrayList<>(topics.size());
        for (com.trackorithm.track.modules.topic.entity.Topic t : topics) {
            TopicDto topicDto = TopicMapper.toDto(t);
            List<SubtopicDto> subs = subtopicsByTopic.getOrDefault(t.getId(), List.of());
            topicTrees.add(new TopicHierarchyDto(topicDto, subs));
        }

        return new SheetHierarchyDto(sheetDto, tags, topicTrees);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicHierarchyDto topicHierarchy(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId) {
        requireReadableSheet(requesterUserId, isAdmin, sheetId);
        com.trackorithm.track.modules.topic.entity.Topic topic = topicRepository.findByIdAndSheet_Id(topicId, sheetId)
                .orElseThrow(() -> new NotFoundException("Topic not found"));

        TopicDto topicDto = TopicMapper.toDto(topic);
        List<SubtopicDto> subtopics = subtopicRepository.findByTopic_IdOrderByOrderIndexAsc(topicId).stream()
                .map(SubtopicMapper::toDto)
                .toList();
        return new TopicHierarchyDto(topicDto, subtopics);
    }

    @Override
    @Transactional(readOnly = true)
    public SubtopicHierarchyDto subtopicHierarchy(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UUID subtopicId) {
        requireReadableSheet(requesterUserId, isAdmin, sheetId);
        com.trackorithm.track.modules.topic.entity.Topic topic = topicRepository.findByIdAndSheet_Id(topicId, sheetId)
                .orElseThrow(() -> new NotFoundException("Topic not found"));

        return subtopicRepository.findByIdAndTopic_Id(subtopicId, topic.getId())
                .map(SubtopicMapper::toDto)
                .map(SubtopicHierarchyDto::new)
                .orElseThrow(() -> new NotFoundException("Subtopic not found"));
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
}

