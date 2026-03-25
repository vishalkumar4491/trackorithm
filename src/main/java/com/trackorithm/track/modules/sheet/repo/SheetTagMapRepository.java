package com.trackorithm.track.modules.sheet.repo;

import com.trackorithm.track.modules.sheet.entity.SheetTagMap;
import com.trackorithm.track.modules.sheet.entity.SheetTagMapId;
import com.trackorithm.track.modules.sheet.entity.SheetTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SheetTagMapRepository extends JpaRepository<SheetTagMap, SheetTagMapId> {

    @Query("""
            select m.tag.id
            from SheetTagMap m
            where m.sheet.id = :sheetId
            """)
    List<UUID> findTagIdsBySheetId(@Param("sheetId") UUID sheetId);

    @Query("""
            select m.tag
            from SheetTagMap m
            where m.sheet.id = :sheetId
            order by m.tag.system desc, lower(m.tag.name) asc
            """)
    List<SheetTag> findTagsBySheetId(@Param("sheetId") UUID sheetId);

    @Modifying
    @Query("""
            delete from SheetTagMap m
            where m.sheet.id = :sheetId
            """)
    void deleteBySheetId(@Param("sheetId") UUID sheetId);

    boolean existsBySheet_IdAndTag_Id(UUID sheetId, UUID tagId);
}
