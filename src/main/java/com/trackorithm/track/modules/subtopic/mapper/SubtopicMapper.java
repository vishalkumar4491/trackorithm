package com.trackorithm.track.modules.subtopic.mapper;

import com.trackorithm.track.modules.subtopic.dto.SubtopicDto;
import com.trackorithm.track.modules.subtopic.entity.Subtopic;

public final class SubtopicMapper {
    private SubtopicMapper() {
    }

    public static SubtopicDto toDto(Subtopic subtopic) {
        return new SubtopicDto(
                subtopic.getId(),
                subtopic.getTopic().getId(),
                subtopic.getTopic().getSheet().getId(),
                subtopic.getName(),
                subtopic.getOrderIndex()
        );
    }
}

