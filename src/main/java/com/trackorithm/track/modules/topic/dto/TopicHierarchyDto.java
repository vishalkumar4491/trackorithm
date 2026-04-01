package com.trackorithm.track.modules.topic.dto;

import com.trackorithm.track.modules.problem.dto.ProblemCardDto;
import com.trackorithm.track.modules.subtopic.dto.SubtopicHierarchyDto;

import java.util.List;

public record TopicHierarchyDto(
        TopicDto topic,
        List<ProblemCardDto> problems,
        List<SubtopicHierarchyDto> subtopics
) {
}
