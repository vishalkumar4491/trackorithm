package com.trackorithm.track.modules.sheet.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentStatusDto(
        UUID sheetId,
        boolean enrolled,
        LocalDateTime enrolledAt
) {
    public static EnrollmentStatusDto enrolled(UUID sheetId, LocalDateTime enrolledAt) {
        return new EnrollmentStatusDto(sheetId, true, enrolledAt);
    }

    public static EnrollmentStatusDto notEnrolled(UUID sheetId) {
        return new EnrollmentStatusDto(sheetId, false, null);
    }
}

