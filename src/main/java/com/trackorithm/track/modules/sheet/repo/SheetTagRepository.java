package com.trackorithm.track.modules.sheet.repo;

import com.trackorithm.track.modules.sheet.entity.SheetTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SheetTagRepository extends JpaRepository<SheetTag, UUID> {
    @Query("""
            select t
            from SheetTag t
            order by t.system desc, lower(t.name) asc
            """)
    List<SheetTag> findVisibleToUser(@Param("userId") UUID userId);

    Optional<SheetTag> findByIdAndSystemTrue(UUID id);

    boolean existsByNameIgnoreCase(String name);

    Optional<SheetTag> findFirstByNameIgnoreCase(String name);
}
