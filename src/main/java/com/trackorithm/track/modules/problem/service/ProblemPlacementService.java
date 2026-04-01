package com.trackorithm.track.modules.problem.service;

import com.trackorithm.track.modules.problem.dto.AddProblemRequest;
import com.trackorithm.track.modules.problem.dto.MoveProblemRequest;
import com.trackorithm.track.modules.problem.dto.ReorderProblemsRequest;

import java.util.List;
import java.util.UUID;

public interface ProblemPlacementService {
    List<UUID> addToTopic(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, AddProblemRequest request);

    List<UUID> addToSubtopic(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UUID subtopicId, AddProblemRequest request);

    List<UUID> reorderTopicProblems(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, ReorderProblemsRequest request);

    List<UUID> reorderSubtopicProblems(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UUID subtopicId, ReorderProblemsRequest request);

    void moveWithinSheet(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID problemId, MoveProblemRequest request);
}

