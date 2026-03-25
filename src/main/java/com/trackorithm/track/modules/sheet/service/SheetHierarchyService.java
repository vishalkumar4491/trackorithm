package com.trackorithm.track.modules.sheet.service;

import com.trackorithm.track.modules.sheet.dto.SheetHierarchyDto;
import com.trackorithm.track.modules.subtopic.dto.SubtopicHierarchyDto;
import com.trackorithm.track.modules.topic.dto.TopicHierarchyDto;

import java.util.UUID;

public interface SheetHierarchyService {
    SheetHierarchyDto sheetHierarchy(UUID requesterUserId, boolean isAdmin, UUID sheetId);

    TopicHierarchyDto topicHierarchy(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId);

    SubtopicHierarchyDto subtopicHierarchy(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UUID subtopicId);
}

