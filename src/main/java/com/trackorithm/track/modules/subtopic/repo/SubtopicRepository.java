package com.trackorithm.track.modules.subtopic.repo;

import com.trackorithm.track.modules.subtopic.entity.Subtopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    int countByTopic_Id(UUID topicId);

    List<Subtopic> findByTopic_IdInOrderByTopic_IdAscOrderIndexAsc(List<UUID> topicIds);

    @Query("""
            select coalesce(max(s.orderIndex), -1)
            from Subtopic s
            where s.topic.id = :topicId
            """)
    int findMaxOrderIndexByTopicId(@Param("topicId") UUID topicId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Subtopic s
            set s.orderIndex = s.orderIndex + 1
            where s.topic.id = :topicId
              and s.orderIndex >= :fromIndex
            """)
    int incrementFrom(@Param("topicId") UUID topicId, @Param("fromIndex") int fromIndex);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Subtopic s
            set s.orderIndex = s.orderIndex - 1
            where s.topic.id = :topicId
              and s.orderIndex > :afterIndex
            """)
    int decrementAfter(@Param("topicId") UUID topicId, @Param("afterIndex") int afterIndex);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Subtopic s
            set s.orderIndex = s.orderIndex + 1
            where s.topic.id = :topicId
              and s.orderIndex >= :fromInclusive
              and s.orderIndex < :toExclusive
              and s.id <> :excludeId
            """)
    int incrementRangeExcluding(@Param("topicId") UUID topicId,
                                @Param("fromInclusive") int fromInclusive,
                                @Param("toExclusive") int toExclusive,
                                @Param("excludeId") UUID excludeId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Subtopic s
            set s.orderIndex = s.orderIndex - 1
            where s.topic.id = :topicId
              and s.orderIndex > :fromExclusive
              and s.orderIndex <= :toInclusive
              and s.id <> :excludeId
            """)
    int decrementRangeExcluding(@Param("topicId") UUID topicId,
                                @Param("fromExclusive") int fromExclusive,
                                @Param("toInclusive") int toInclusive,
                                @Param("excludeId") UUID excludeId);
}
