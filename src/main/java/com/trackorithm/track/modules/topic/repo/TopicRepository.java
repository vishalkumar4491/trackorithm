package com.trackorithm.track.modules.topic.repo;

import com.trackorithm.track.modules.topic.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {

    Page<Topic> findBySheet_IdOrderByOrderIndexAsc(UUID sheetId, Pageable pageable);

    List<Topic> findBySheet_IdOrderByOrderIndexAsc(UUID sheetId);

    Optional<Topic> findByIdAndSheet_Id(UUID topicId, UUID sheetId);

    boolean existsBySheet_IdAndNameIgnoreCase(UUID sheetId, String name);

    // find max (last) order index by sheet id
    @Query("""
            select coalesce(max(t.orderIndex), -1)
            from Topic t
            where t.sheet.id = :sheetId
            """)
    int findMaxOrderIndexBySheetId(@Param("sheetId") UUID sheetId);
}

