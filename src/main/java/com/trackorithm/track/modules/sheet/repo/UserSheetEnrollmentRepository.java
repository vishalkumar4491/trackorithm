package com.trackorithm.track.modules.sheet.repo;

import com.trackorithm.track.modules.sheet.entity.UserSheetEnrollment;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserSheetEnrollmentRepository extends JpaRepository<UserSheetEnrollment, UUID> {

    @Query("""
            select e
            from UserSheetEnrollment e
            where e.user.id = :userId
              and e.sheet.id = :sheetId
            """)
    Optional<UserSheetEnrollment> findByUserIdAndSheetId(@Param("userId") UUID userId, @Param("sheetId") UUID sheetId);

    @Query("""
            select e.sheet
            from UserSheetEnrollment e
            where e.user.id = :userId
              and e.removedAt is null
            """)
    Page<Sheet> findActiveSheetsByUserId(@Param("userId") UUID userId, Pageable pageable);
}
