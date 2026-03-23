package com.trackorithm.track.modules.sheet.service;

import com.trackorithm.track.modules.sheet.dto.EnrollmentStatusDto;

import java.util.UUID;

public interface EnrollmentService {
    EnrollmentStatusDto enroll(UUID userId, UUID sheetId);

    EnrollmentStatusDto unenroll(UUID userId, UUID sheetId);

    EnrollmentStatusDto enrollmentStatus(UUID userId, UUID sheetId);
}

