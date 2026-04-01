package com.trackorithm.track.modules.problem.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.exception.ConflictException;
import com.trackorithm.track.common.exception.ForbiddenException;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.modules.problem.dto.AddProblemRequest;
import com.trackorithm.track.modules.problem.dto.MoveProblemRequest;
import com.trackorithm.track.modules.problem.dto.ReorderProblemsRequest;
import com.trackorithm.track.modules.problem.entity.SubtopicProblem;
import com.trackorithm.track.modules.problem.entity.SubtopicProblemId;
import com.trackorithm.track.modules.problem.entity.TopicProblem;
import com.trackorithm.track.modules.problem.entity.TopicProblemId;
import com.trackorithm.track.modules.problem.repo.ProblemRepository;
import com.trackorithm.track.modules.problem.repo.SubtopicProblemRepository;
import com.trackorithm.track.modules.problem.repo.TopicProblemRepository;
import com.trackorithm.track.modules.problem.service.ProblemPlacementService;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import com.trackorithm.track.modules.sheet.repo.SheetRepository;
import com.trackorithm.track.modules.subtopic.entity.Subtopic;
import com.trackorithm.track.modules.subtopic.repo.SubtopicRepository;
import com.trackorithm.track.modules.topic.entity.Topic;
import com.trackorithm.track.modules.topic.repo.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class ProblemPlacementServiceImpl implements ProblemPlacementService {
    private final SheetRepository sheetRepository;
    private final TopicRepository topicRepository;
    private final SubtopicRepository subtopicRepository;
    private final ProblemRepository problemRepository;
    private final TopicProblemRepository topicProblemRepository;
    private final SubtopicProblemRepository subtopicProblemRepository;

    @Override
    @Transactional
    public List<UUID> addToTopic(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, AddProblemRequest request) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);
        Topic topic = topicRepository.findByIdAndSheet_Id(topicId, sheetId).orElseThrow(() -> new NotFoundException("Topic not found"));

        if (subtopicRepository.countByTopic_Id(topicId) > 0) {
            throw new ConflictException("This topic uses subtopics; add problems under a subtopic");
        }

        UUID problemId = request.problemId();
        if (!problemRepository.existsById(problemId)) {
            throw new NotFoundException("Problem not found");
        }

        // TODO : Enforce one placement per sheet (simple rule for move/UX) I will update later.
        if (isProblemAlreadyInSheet(sheetId, problemId)) {
            throw new ConflictException("Problem already exists in this sheet");
        }

        int count = topicProblemRepository.countByTopic_Id(topicId);
        int pos = clampInsertPosition(request.position(), count);

        topicProblemRepository.incrementFrom(topicId, pos);

        TopicProblem tp = new TopicProblem();
        tp.setId(new TopicProblemId(topicId, problemId));
        tp.setTopic(topic);
        tp.setProblem(problemRepository.getReferenceById(problemId));
        tp.setOrderIndex(pos);
        topicProblemRepository.save(tp);

        return topicProblemRepository.findByTopic_IdOrderByOrderIndexAsc(topicId).stream().map(x -> x.getProblem().getId()).toList();
    }

    @Override
    @Transactional
    public List<UUID> addToSubtopic(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UUID subtopicId, AddProblemRequest request) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);
        topicRepository.findByIdAndSheet_Id(topicId, sheetId).orElseThrow(() -> new NotFoundException("Topic not found"));
        Subtopic subtopic = subtopicRepository.findByIdAndTopic_Id(subtopicId, topicId).orElseThrow(() -> new NotFoundException("Subtopic not found"));

        UUID problemId = request.problemId();
        if (!problemRepository.existsById(problemId)) {
            throw new NotFoundException("Problem not found");
        }
        if (isProblemAlreadyInSheet(sheetId, problemId)) {
            throw new ConflictException("Problem already exists in this sheet");
        }

        int count = subtopicProblemRepository.countBySubtopic_Id(subtopicId);
        int pos = clampInsertPosition(request.position(), count);

        subtopicProblemRepository.incrementFrom(subtopicId, pos);

        SubtopicProblem sp = new SubtopicProblem();
        sp.setId(new SubtopicProblemId(subtopicId, problemId));
        sp.setSubtopic(subtopic);
        sp.setProblem(problemRepository.getReferenceById(problemId));
        sp.setOrderIndex(pos);
        subtopicProblemRepository.save(sp);

        return subtopicProblemRepository.findBySubtopic_IdOrderByOrderIndexAsc(subtopicId).stream().map(x -> x.getProblem().getId()).toList();
    }

    @Override
    @Transactional
    public List<UUID> reorderTopicProblems(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, ReorderProblemsRequest request) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);
        Topic topic = topicRepository.findByIdAndSheet_Id(topicId, sheetId).orElseThrow(() -> new NotFoundException("Topic not found"));
        if (subtopicRepository.countByTopic_Id(topicId) > 0) {
            throw new ConflictException("This topic uses subtopics; reorder subtopic problems");
        }

        List<UUID> ordered = request.orderedProblemIds();
        if (ordered.size() != new HashSet<>(ordered).size()) {
            throw new ConflictException("Duplicate problem ids in reorder request");
        }

        List<TopicProblem> existing = topicProblemRepository.findByTopic_IdOrderByOrderIndexAsc(topic.getId());
        if (existing.size() != ordered.size()) {
            throw new ConflictException("Reorder request must include all problems in the topic");
        }

        Map<UUID, TopicProblem> byProblem = new HashMap<>();
        for (TopicProblem tp : existing) {
            byProblem.put(tp.getProblem().getId(), tp);
        }
        for (UUID pid : ordered) {
            if (!byProblem.containsKey(pid)) {
                throw new ConflictException("Reorder request contains problem not in topic");
            }
        }

        int tmp = -1;
        List<TopicProblem> inOrder = new ArrayList<>(ordered.size());
        for (UUID pid : ordered) {
            TopicProblem tp = byProblem.get(pid);
            tp.setOrderIndex(tmp--);
            inOrder.add(tp);
        }
        topicProblemRepository.saveAll(inOrder);
        topicProblemRepository.flush();

        for (int i = 0; i < inOrder.size(); i++) {
            inOrder.get(i).setOrderIndex(i);
        }
        topicProblemRepository.saveAll(inOrder);
        topicProblemRepository.flush();

        return inOrder.stream().map(x -> x.getProblem().getId()).toList();
    }

    @Override
    @Transactional
    public List<UUID> reorderSubtopicProblems(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UUID subtopicId, ReorderProblemsRequest request) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);
        topicRepository.findByIdAndSheet_Id(topicId, sheetId).orElseThrow(() -> new NotFoundException("Topic not found"));
        subtopicRepository.findByIdAndTopic_Id(subtopicId, topicId).orElseThrow(() -> new NotFoundException("Subtopic not found"));

        List<UUID> ordered = request.orderedProblemIds();
        if (ordered.size() != new HashSet<>(ordered).size()) {
            throw new ConflictException("Duplicate problem ids in reorder request");
        }

        List<SubtopicProblem> existing = subtopicProblemRepository.findBySubtopic_IdOrderByOrderIndexAsc(subtopicId);
        if (existing.size() != ordered.size()) {
            throw new ConflictException("Reorder request must include all problems in the subtopic");
        }

        Map<UUID, SubtopicProblem> byProblem = new HashMap<>();
        for (SubtopicProblem sp : existing) {
            byProblem.put(sp.getProblem().getId(), sp);
        }
        for (UUID pid : ordered) {
            if (!byProblem.containsKey(pid)) {
                throw new ConflictException("Reorder request contains problem not in subtopic");
            }
        }

        int tmp = -1;
        List<SubtopicProblem> inOrder = new ArrayList<>(ordered.size());
        for (UUID pid : ordered) {
            SubtopicProblem sp = byProblem.get(pid);
            sp.setOrderIndex(tmp--);
            inOrder.add(sp);
        }
        subtopicProblemRepository.saveAll(inOrder);
        subtopicProblemRepository.flush();

        for (int i = 0; i < inOrder.size(); i++) {
            inOrder.get(i).setOrderIndex(i);
        }
        subtopicProblemRepository.saveAll(inOrder);
        subtopicProblemRepository.flush();

        return inOrder.stream().map(x -> x.getProblem().getId()).toList();
    }

    @Override
    @Transactional
    public void moveWithinSheet(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID problemId, MoveProblemRequest request) {
        requireWritableSheet(requesterUserId, isAdmin, sheetId);
        if (!problemRepository.existsById(problemId)) {
            throw new NotFoundException("Problem not found");
        }

        boolean toTopic = request.targetTopicId() != null;
        boolean toSubtopic = request.targetSubtopicId() != null;
        if (toTopic == toSubtopic) {
            throw new ConflictException("Provide exactly one of targetTopicId or targetSubtopicId");
        }

        TopicProblem existingTopic = topicProblemRepository.findFirstByTopic_Sheet_IdAndProblem_Id(sheetId, problemId).orElse(null);
        SubtopicProblem existingSub = subtopicProblemRepository.findFirstBySubtopic_Topic_Sheet_IdAndProblem_Id(sheetId, problemId).orElse(null);
        if (existingTopic == null && existingSub == null) {
            throw new NotFoundException("Problem is not placed in this sheet");
        }
        if (existingTopic != null && existingSub != null) {
            throw new ConflictException("Problem is placed multiple times in this sheet");
        }

        if (toTopic) {
            UUID targetTopicId = request.targetTopicId();
            Topic targetTopic = topicRepository.findByIdAndSheet_Id(targetTopicId, sheetId).orElseThrow(() -> new NotFoundException("Target topic not found"));
            if (subtopicRepository.countByTopic_Id(targetTopicId) > 0) {
                throw new ConflictException("Target topic uses subtopics; move under a subtopic");
            }

            int targetCount = topicProblemRepository.countByTopic_Id(targetTopicId);
            int insertPos = clampInsertPosition(request.position(), targetCount);

            // remove from source
            if (existingTopic != null) {
                UUID srcTopicId = existingTopic.getTopic().getId();
                int oldPos = existingTopic.getOrderIndex();
                if (srcTopicId.equals(targetTopicId)) {
                    // within same topic: just shift to position using range updates
                    int maxIndex = Math.max(0, targetCount - 1);
                    int newPos = clampMovePosition(request.position(), maxIndex, oldPos);
                    if (newPos == oldPos) return;
                    if (newPos < oldPos) {
                        topicProblemRepository.incrementRangeExcluding(srcTopicId, newPos, oldPos, problemId);
                    } else {
                        topicProblemRepository.decrementRangeExcluding(srcTopicId, oldPos, newPos, problemId);
                    }
                    existingTopic.setOrderIndex(newPos);
                    topicProblemRepository.save(existingTopic);
                    return;
                }
                topicProblemRepository.deleteByTopicAndProblem(srcTopicId, problemId);
                topicProblemRepository.decrementAfter(srcTopicId, oldPos);
            } else {
                UUID srcSubId = existingSub.getSubtopic().getId();
                int oldPos = existingSub.getOrderIndex();
                subtopicProblemRepository.deleteBySubtopicAndProblem(srcSubId, problemId);
                subtopicProblemRepository.decrementAfter(srcSubId, oldPos);
            }

            // insert into target
            topicProblemRepository.incrementFrom(targetTopicId, insertPos);
            TopicProblem tp = new TopicProblem();
            tp.setId(new TopicProblemId(targetTopicId, problemId));
            tp.setTopic(targetTopic);
            tp.setProblem(problemRepository.getReferenceById(problemId));
            tp.setOrderIndex(insertPos);
            topicProblemRepository.save(tp);
            return;
        }

        UUID targetSubtopicId = request.targetSubtopicId();
        Subtopic targetSub = subtopicRepository.findByIdAndTopic_Sheet_Id(targetSubtopicId, sheetId)
                .orElseThrow(() -> new NotFoundException("Target subtopic not found"));

        int targetCount = subtopicProblemRepository.countBySubtopic_Id(targetSubtopicId);
        int insertPos = clampInsertPosition(request.position(), targetCount);

        if (existingSub != null) {
            UUID srcSubId = existingSub.getSubtopic().getId();
            int oldPos = existingSub.getOrderIndex();
            if (srcSubId.equals(targetSubtopicId)) {
                int maxIndex = Math.max(0, targetCount - 1);
                int newPos = clampMovePosition(request.position(), maxIndex, oldPos);
                if (newPos == oldPos) return;
                if (newPos < oldPos) {
                    subtopicProblemRepository.incrementRangeExcluding(srcSubId, newPos, oldPos, problemId);
                } else {
                    subtopicProblemRepository.decrementRangeExcluding(srcSubId, oldPos, newPos, problemId);
                }
                existingSub.setOrderIndex(newPos);
                subtopicProblemRepository.save(existingSub);
                return;
            }
            subtopicProblemRepository.deleteBySubtopicAndProblem(srcSubId, problemId);
            subtopicProblemRepository.decrementAfter(srcSubId, oldPos);
        } else {
            UUID srcTopicId = existingTopic.getTopic().getId();
            int oldPos = existingTopic.getOrderIndex();
            topicProblemRepository.deleteByTopicAndProblem(srcTopicId, problemId);
            topicProblemRepository.decrementAfter(srcTopicId, oldPos);
        }

        subtopicProblemRepository.incrementFrom(targetSubtopicId, insertPos);
        SubtopicProblem sp = new SubtopicProblem();
        sp.setId(new SubtopicProblemId(targetSubtopicId, problemId));
        sp.setSubtopic(targetSub);
        sp.setProblem(problemRepository.getReferenceById(problemId));
        sp.setOrderIndex(insertPos);
        subtopicProblemRepository.save(sp);
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

    private boolean isProblemAlreadyInSheet(UUID sheetId, UUID problemId) {
        return topicProblemRepository.findFirstByTopic_Sheet_IdAndProblem_Id(sheetId, problemId).isPresent()
                || subtopicProblemRepository.findFirstBySubtopic_Topic_Sheet_IdAndProblem_Id(sheetId, problemId).isPresent();
    }

    private static int clampInsertPosition(Integer requested, int size) {
        if (requested == null) return size;
        if (requested < 0) return 0;
        if (requested > size) return size;
        return requested;
    }

    private static int clampMovePosition(Integer requested, int maxIndex, int current) {
        if (requested == null) return current;
        if (requested < 0) return 0;
        if (requested > maxIndex) return maxIndex;
        return requested;
    }
}

