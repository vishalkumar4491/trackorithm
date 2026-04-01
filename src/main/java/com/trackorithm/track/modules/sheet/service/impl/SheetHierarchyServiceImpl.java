package com.trackorithm.track.modules.sheet.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.enums.Visibility;
import com.trackorithm.track.common.exception.ForbiddenException;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.modules.problem.dto.ProblemCardDto;
import com.trackorithm.track.modules.problem.entity.Problem;
import com.trackorithm.track.modules.problem.repo.SubtopicProblemRepository;
import com.trackorithm.track.modules.problem.repo.TopicProblemRepository;
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
import com.trackorithm.track.modules.tracking.entity.UserProblemStatus;
import com.trackorithm.track.modules.tracking.repo.UserProblemStatusRepository;
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
    private final TopicProblemRepository topicProblemRepository;
    private final SubtopicProblemRepository subtopicProblemRepository;
    private final UserProblemStatusRepository userProblemStatusRepository;

    public SheetHierarchyServiceImpl(SheetRepository sheetRepository,
                                    SheetTagMapRepository sheetTagMapRepository,
                                    TopicRepository topicRepository,
                                    SubtopicRepository subtopicRepository,
                                    TopicProblemRepository topicProblemRepository,
                                    SubtopicProblemRepository subtopicProblemRepository,
                                    UserProblemStatusRepository userProblemStatusRepository) {
        this.sheetRepository = sheetRepository;
        this.sheetTagMapRepository = sheetTagMapRepository;
        this.topicRepository = topicRepository;
        this.subtopicRepository = subtopicRepository;
        this.topicProblemRepository = topicProblemRepository;
        this.subtopicProblemRepository = subtopicProblemRepository;
        this.userProblemStatusRepository = userProblemStatusRepository;
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

        List<com.trackorithm.track.modules.subtopic.entity.Subtopic> subtopics = topicIds.isEmpty()
                ? List.of()
                : subtopicRepository.findByTopic_IdInOrderByTopic_IdAscOrderIndexAsc(topicIds);

        Map<UUID, List<com.trackorithm.track.modules.subtopic.entity.Subtopic>> subtopicsByTopic = new HashMap<>();
        for (var s : subtopics) {
            subtopicsByTopic.computeIfAbsent(s.getTopic().getId(), k -> new ArrayList<>()).add(s);
        }

        List<UUID> subtopicIds = subtopics.stream().map(s -> s.getId()).toList();

        Map<UUID, List<Problem>> topicProblemsByTopic = new HashMap<>();
        if (!topicIds.isEmpty()) {
            topicProblemRepository.findByTopicIdsWithProblem(topicIds).forEach(tp -> {
                topicProblemsByTopic.computeIfAbsent(tp.getTopic().getId(), k -> new ArrayList<>()).add(tp.getProblem());
            });
        }

        Map<UUID, List<Problem>> subtopicProblemsBySubtopic = new HashMap<>();
        if (!subtopicIds.isEmpty()) {
            subtopicProblemRepository.findBySubtopicIdsWithProblem(subtopicIds).forEach(sp -> {
                subtopicProblemsBySubtopic.computeIfAbsent(sp.getSubtopic().getId(), k -> new ArrayList<>()).add(sp.getProblem());
            });
        }

        // Batch overlay for the whole sheet.
        Set<UUID> problemIds = new HashSet<>();
        topicProblemsByTopic.values().forEach(list -> list.forEach(p -> problemIds.add(p.getId())));
        subtopicProblemsBySubtopic.values().forEach(list -> list.forEach(p -> problemIds.add(p.getId())));

        Map<UUID, UserProblemStatus> overlayByProblem = new HashMap<>();
        if (!problemIds.isEmpty()) {
            userProblemStatusRepository.findByUserIdAndProblemIdIn(requesterUserId, new ArrayList<>(problemIds))
                    .forEach(ups -> overlayByProblem.put(ups.getProblem().getId(), ups));
        }

        List<TopicHierarchyDto> topicTrees = new ArrayList<>(topics.size());
        for (com.trackorithm.track.modules.topic.entity.Topic t : topics) {
            TopicDto topicDto = TopicMapper.toDto(t);

            List<ProblemCardDto> topicProblems = topicProblemsByTopic.getOrDefault(t.getId(), List.of()).stream()
                    .map(p -> toCard(p, overlayByProblem.get(p.getId())))
                    .toList();

            List<SubtopicHierarchyDto> subs = subtopicsByTopic.getOrDefault(t.getId(), List.of()).stream()
                    .map(s -> {
                        SubtopicDto subDto = SubtopicMapper.toDto(s);
                        List<ProblemCardDto> subProblems = subtopicProblemsBySubtopic.getOrDefault(s.getId(), List.of()).stream()
                                .map(p -> toCard(p, overlayByProblem.get(p.getId())))
                                .toList();
                        return new SubtopicHierarchyDto(subDto, subProblems);
                    })
                    .toList();

            topicTrees.add(new TopicHierarchyDto(topicDto, topicProblems, subs));
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

        List<com.trackorithm.track.modules.subtopic.entity.Subtopic> subtopics = subtopicRepository.findByTopic_IdOrderByOrderIndexAsc(topicId);
        List<UUID> subtopicIds = subtopics.stream().map(s -> s.getId()).toList();

        Map<UUID, List<Problem>> topicProblemsByTopic = new HashMap<>();
        topicProblemRepository.findByTopicIdsWithProblem(List.of(topicId)).forEach(tp -> {
            topicProblemsByTopic.computeIfAbsent(tp.getTopic().getId(), k -> new ArrayList<>()).add(tp.getProblem());
        });

        Map<UUID, List<Problem>> subtopicProblemsBySubtopic = new HashMap<>();
        if (!subtopicIds.isEmpty()) {
            subtopicProblemRepository.findBySubtopicIdsWithProblem(subtopicIds).forEach(sp -> {
                subtopicProblemsBySubtopic.computeIfAbsent(sp.getSubtopic().getId(), k -> new ArrayList<>()).add(sp.getProblem());
            });
        }

        Set<UUID> problemIds = new HashSet<>();
        topicProblemsByTopic.values().forEach(list -> list.forEach(p -> problemIds.add(p.getId())));
        subtopicProblemsBySubtopic.values().forEach(list -> list.forEach(p -> problemIds.add(p.getId())));

        Map<UUID, UserProblemStatus> overlayByProblem = new HashMap<>();
        if (!problemIds.isEmpty()) {
            userProblemStatusRepository.findByUserIdAndProblemIdIn(requesterUserId, new ArrayList<>(problemIds))
                    .forEach(ups -> overlayByProblem.put(ups.getProblem().getId(), ups));
        }

        List<ProblemCardDto> topicProblems = topicProblemsByTopic.getOrDefault(topicId, List.of()).stream()
                .map(p -> toCard(p, overlayByProblem.get(p.getId())))
                .toList();

        List<SubtopicHierarchyDto> subTrees = subtopics.stream()
                .map(s -> {
                    SubtopicDto subDto = SubtopicMapper.toDto(s);
                    List<ProblemCardDto> subProblems = subtopicProblemsBySubtopic.getOrDefault(s.getId(), List.of()).stream()
                            .map(p -> toCard(p, overlayByProblem.get(p.getId())))
                            .toList();
                    return new SubtopicHierarchyDto(subDto, subProblems);
                })
                .toList();

        return new TopicHierarchyDto(topicDto, topicProblems, subTrees);
    }

    @Override
    @Transactional(readOnly = true)
    public SubtopicHierarchyDto subtopicHierarchy(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UUID subtopicId) {
        requireReadableSheet(requesterUserId, isAdmin, sheetId);
        com.trackorithm.track.modules.topic.entity.Topic topic = topicRepository.findByIdAndSheet_Id(topicId, sheetId)
                .orElseThrow(() -> new NotFoundException("Topic not found"));

        var subtopic = subtopicRepository.findByIdAndTopic_Id(subtopicId, topic.getId())
                .orElseThrow(() -> new NotFoundException("Subtopic not found"));

        var problems = subtopicProblemRepository.findBySubtopicIdsWithProblem(List.of(subtopicId)).stream()
                .map(sp -> sp.getProblem())
                .toList();
        Map<UUID, UserProblemStatus> overlayByProblem = new HashMap<>();
        if (!problems.isEmpty()) {
            userProblemStatusRepository.findByUserIdAndProblemIdIn(requesterUserId, problems.stream().map(Problem::getId).toList())
                    .forEach(ups -> overlayByProblem.put(ups.getProblem().getId(), ups));
        }

        List<ProblemCardDto> cards = problems.stream().map(p -> toCard(p, overlayByProblem.get(p.getId()))).toList();
        return new SubtopicHierarchyDto(SubtopicMapper.toDto(subtopic), cards);
    }

    private static ProblemCardDto toCard(Problem p, UserProblemStatus ups) {
        boolean bookmarked = ups != null && Boolean.TRUE.equals(ups.getIsBookmarked());
        String status = ups != null && ups.getStatus() != null ? ups.getStatus().name() : "TODO";
        return new ProblemCardDto(
                p.getId(),
                p.getTitle(),
                p.getSlug(),
                p.getPlatform(),
                p.getDifficulty(),
                p.getProblemUrl(),
                status,
                bookmarked
        );
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
