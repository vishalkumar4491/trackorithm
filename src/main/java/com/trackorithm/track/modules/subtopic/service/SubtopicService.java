package com.trackorithm.track.modules.subtopic.service;

import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.subtopic.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SubtopicService {
    PageResponse<SubtopicDto> list(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, Pageable pageable);

    SubtopicDto create(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, CreateSubtopicRequest request);

    SubtopicDto update(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UUID subtopicId, UpdateSubtopicRequest request);

    void delete(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UUID subtopicId);

    List<SubtopicDto> reorder(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, ReorderSubtopicsRequest request);

    SubtopicDto move(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID subtopicId, MoveSubtopicRequest request);
}

