package com.trackorithm.track.modules.subtopic.repo;

import com.trackorithm.track.modules.subtopic.entity.Subtopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubtopicRepository extends JpaRepository<Subtopic, UUID> {
    Page<Subtopic> findByTopic_IdOrderByOrderIndexAsc(UUID topicId, Pageable pageable);

    List<Subtopic> findByTopic_IdOrderByOrderIndexAsc(UUID topicId);

    Optional<Subtopic> findByIdAndTopic_Id(UUID subtopicId, UUID topicId);

    boolean existsByTopic_IdAndNameIgnoreCase(UUID topicId, String name);

    @Query("""
            select coalesce(max(s.orderIndex), -1)
            from Subtopic s
            where s.topic.id = :topicId
            """)
    int findMaxOrderIndexByTopicId(@Param("topicId") UUID topicId);
}

