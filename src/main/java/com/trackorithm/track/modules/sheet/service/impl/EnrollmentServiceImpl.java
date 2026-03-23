package com.trackorithm.track.modules.sheet.service.impl;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.modules.sheet.dto.EnrollmentStatusDto;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import com.trackorithm.track.modules.sheet.entity.UserSheetEnrollment;
import com.trackorithm.track.modules.sheet.repo.SheetRepository;
import com.trackorithm.track.modules.sheet.repo.UserSheetEnrollmentRepository;
import com.trackorithm.track.modules.sheet.service.EnrollmentService;
import com.trackorithm.track.modules.user.entity.User;
import com.trackorithm.track.modules.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final UserSheetEnrollmentRepository enrollmentRepository;
    private final SheetRepository sheetRepository;
    private final UserRepository userRepository;

    public EnrollmentServiceImpl(UserSheetEnrollmentRepository enrollmentRepository,
                                 SheetRepository sheetRepository,
                                 UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.sheetRepository = sheetRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public EnrollmentStatusDto enroll(UUID userId, UUID sheetId) {
        Sheet sheet = sheetRepository.findByIdAndType(sheetId, SheetType.SYSTEM)
                .orElseThrow(() -> new NotFoundException("System sheet not found"));

        UserSheetEnrollment enrollment = enrollmentRepository.findByUserIdAndSheetId(userId, sheetId)
                .orElseGet(() -> {
                    UserSheetEnrollment e = new UserSheetEnrollment();
                    User userRef = userRepository.getReferenceById(userId);
                    e.setUser(userRef);
                    e.setSheet(sheet);
                    e.setEnrolledAt(LocalDateTime.now());
                    return e;
                });

        if (enrollment.getRemovedAt() != null) {
            enrollment.setRemovedAt(null);
            enrollment.setEnrolledAt(LocalDateTime.now());
        }

        enrollmentRepository.save(enrollment);
        return EnrollmentStatusDto.enrolled(sheetId, enrollment.getEnrolledAt());
    }

    @Override
    @Transactional
    public EnrollmentStatusDto unenroll(UUID userId, UUID sheetId) {
        UserSheetEnrollment enrollment = enrollmentRepository.findByUserIdAndSheetId(userId, sheetId)
                .orElse(null);

        if (enrollment == null || enrollment.getRemovedAt() != null) {
            return EnrollmentStatusDto.notEnrolled(sheetId);
        }

        enrollment.setRemovedAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
        return EnrollmentStatusDto.notEnrolled(sheetId);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentStatusDto enrollmentStatus(UUID userId, UUID sheetId) {
        return enrollmentRepository.findByUserIdAndSheetId(userId, sheetId)
                .filter(e -> e.getRemovedAt() == null)
                .map(e -> EnrollmentStatusDto.enrolled(sheetId, e.getEnrolledAt()))
                .orElseGet(() -> EnrollmentStatusDto.notEnrolled(sheetId));
    }
}
