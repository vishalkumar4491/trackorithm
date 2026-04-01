package com.trackorithm.track.modules.subtopic.dto;

import com.trackorithm.track.modules.problem.dto.ProblemCardDto;

import java.util.List;

public record SubtopicHierarchyDto(
        SubtopicDto subtopic,
        List<ProblemCardDto> problems
) {
}
