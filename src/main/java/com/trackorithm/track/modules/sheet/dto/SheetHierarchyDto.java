package com.trackorithm.track.modules.sheet.dto;

import com.trackorithm.track.modules.topic.dto.TopicHierarchyDto;

import java.util.List;

public record SheetHierarchyDto(
        SheetSummaryDto sheet,
        List<SheetTagDto> tags,
        List<TopicHierarchyDto> topics
) {
}

