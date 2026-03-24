package com.trackorithm.track.modules.subtopic.dto;

import java.util.UUID;

public record SubtopicDto(
        UUID id,
        UUID topicId,
        UUID sheetId,
        String name,
        int orderIndex
) {
}

