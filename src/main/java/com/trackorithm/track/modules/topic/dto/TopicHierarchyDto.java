package com.trackorithm.track.modules.topic.dto;

import com.trackorithm.track.modules.subtopic.dto.SubtopicDto;

import java.util.List;

public record TopicHierarchyDto(
        TopicDto topic,
        List<SubtopicDto> subtopics
) {
}

