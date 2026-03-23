package com.trackorithm.track.modules.topic.service;

import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.topic.dto.CreateTopicRequest;
import com.trackorithm.track.modules.topic.dto.ReorderTopicsRequest;
import com.trackorithm.track.modules.topic.dto.TopicDto;
import com.trackorithm.track.modules.topic.dto.UpdateTopicRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TopicService {
    PageResponse<TopicDto> list(UUID requesterUserId, boolean isAdmin, UUID sheetId, Pageable pageable);

    TopicDto create(UUID requesterUserId, boolean isAdmin, UUID sheetId, CreateTopicRequest request);

    TopicDto update(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId, UpdateTopicRequest request);

    void delete(UUID requesterUserId, boolean isAdmin, UUID sheetId, UUID topicId);

    List<TopicDto> reorder(UUID requesterUserId, boolean isAdmin, UUID sheetId, ReorderTopicsRequest request);
}

