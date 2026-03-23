package com.trackorithm.track.modules.topic.dto;

import java.util.UUID;

public record TopicDto(
        UUID id,
        UUID sheetId,
        String name,
        int orderIndex
) {
}

