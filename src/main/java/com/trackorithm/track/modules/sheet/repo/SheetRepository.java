package com.trackorithm.track.modules.sheet.repo;

import com.trackorithm.track.common.enums.SheetType;
import com.trackorithm.track.common.enums.Visibility;
import com.trackorithm.track.modules.sheet.entity.Sheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SheetRepository extends JpaRepository<Sheet, UUID> {

    Page<Sheet> findByType(SheetType type, Pageable pageable);

    Page<Sheet> findByTypeAndVisibility(SheetType type, Visibility visibility, Pageable pageable);

    Page<Sheet> findByTypeAndCreatedById(SheetType type, UUID createdById, Pageable pageable);

    boolean existsByTypeAndNameIgnoreCase(SheetType type, String name);

    boolean existsByTypeAndCreatedBy_IdAndNameIgnoreCase(SheetType type, UUID createdById, String name);

    @Query("""
            select (count(s) > 0)
            from Sheet s
            where s.type = :type
              and lower(s.name) = lower(:name)
              and s.id <> :sheetId
            """)
    boolean existsOtherSystemSheetWithName(@Param("type") SheetType type,
                                          @Param("name") String name,
                                          @Param("sheetId") UUID sheetId);

    @Query("""
            select (count(s) > 0)
            from Sheet s
            where s.type = :type
              and s.createdBy.id = :userId
              and lower(s.name) = lower(:name)
              and s.id <> :sheetId
            """)
    boolean existsOtherOwnedSheetWithName(@Param("type") SheetType type,
                                          @Param("userId") UUID userId,
                                          @Param("name") String name,
                                          @Param("sheetId") UUID sheetId);

    @Query("""
            select s
            from Sheet s
            where s.id = :sheetId
              and s.type = :type
            """)
    Optional<Sheet> findByIdAndType(@Param("sheetId") UUID sheetId, @Param("type") SheetType type);

    @Query("""
            select s
            from Sheet s
            where s.id = :sheetId
              and s.type = :type
              and s.createdBy.id = :userId
            """)
    Optional<Sheet> findOwnedUserSheet(@Param("sheetId") UUID sheetId,
                                       @Param("type") SheetType type,
                                       @Param("userId") UUID userId);
}
