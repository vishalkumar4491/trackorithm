package com.trackorithm.track.modules.topic.mapper;

import com.trackorithm.track.modules.topic.dto.TopicDto;
import com.trackorithm.track.modules.topic.entity.Topic;

public final class TopicMapper {
    private TopicMapper() {
    }

    public static TopicDto toDto(Topic topic) {
        return new TopicDto(
                topic.getId(),
                topic.getSheet().getId(),
                topic.getName(),
                topic.getOrderIndex()
        );
    }
}

