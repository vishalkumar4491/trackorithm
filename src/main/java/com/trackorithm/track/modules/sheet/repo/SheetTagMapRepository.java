package com.trackorithm.track.modules.sheet.repo;

import com.trackorithm.track.modules.sheet.entity.SheetTagMap;
import com.trackorithm.track.modules.sheet.entity.SheetTagMapId;
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

    @Modifying
    @Query("""
            delete from SheetTagMap m
            where m.sheet.id = :sheetId
            """)
    void deleteBySheetId(@Param("sheetId") UUID sheetId);
}

