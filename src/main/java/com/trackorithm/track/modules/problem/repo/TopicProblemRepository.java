package com.trackorithm.track.modules.problem.repo;

import com.trackorithm.track.modules.problem.entity.TopicProblem;
import com.trackorithm.track.modules.problem.entity.TopicProblemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TopicProblemRepository extends JpaRepository<TopicProblem, TopicProblemId> {
    List<TopicProblem> findByTopic_IdOrderByOrderIndexAsc(UUID topicId);

    @Query("""
            select tp
            from TopicProblem tp
            join fetch tp.problem p
            where tp.topic.id in :topicIds
            order by tp.topic.id asc, tp.orderIndex asc
            """)
    List<TopicProblem> findByTopicIdsWithProblem(@Param("topicIds") List<UUID> topicIds);

    int countByTopic_Id(UUID topicId);

    java.util.Optional<TopicProblem> findFirstByTopic_Sheet_IdAndProblem_Id(UUID sheetId, UUID problemId);

    @Modifying
    @Query("""
            delete from TopicProblem tp
            where tp.topic.id = :topicId
              and tp.problem.id = :problemId
            """)
    int deleteByTopicAndProblem(@Param("topicId") UUID topicId, @Param("problemId") UUID problemId);

    @Modifying
    @Query("""
            update TopicProblem tp
            set tp.orderIndex = tp.orderIndex + 1
            where tp.topic.id = :topicId
              and tp.orderIndex >= :fromIndex
            """)
    int incrementFrom(@Param("topicId") UUID topicId, @Param("fromIndex") int fromIndex);

    @Modifying
    @Query("""
            update TopicProblem tp
            set tp.orderIndex = tp.orderIndex - 1
            where tp.topic.id = :topicId
              and tp.orderIndex > :afterIndex
            """)
    int decrementAfter(@Param("topicId") UUID topicId, @Param("afterIndex") int afterIndex);

    @Modifying
    @Query("""
            update TopicProblem tp
            set tp.orderIndex = tp.orderIndex + 1
            where tp.topic.id = :topicId
              and tp.orderIndex >= :fromInclusive
              and tp.orderIndex < :toExclusive
              and tp.problem.id <> :excludeProblemId
            """)
    int incrementRangeExcluding(@Param("topicId") UUID topicId,
                                @Param("fromInclusive") int fromInclusive,
                                @Param("toExclusive") int toExclusive,
                                @Param("excludeProblemId") UUID excludeProblemId);

    @Modifying
    @Query("""
            update TopicProblem tp
            set tp.orderIndex = tp.orderIndex - 1
            where tp.topic.id = :topicId
              and tp.orderIndex > :fromExclusive
              and tp.orderIndex <= :toInclusive
              and tp.problem.id <> :excludeProblemId
            """)
    int decrementRangeExcluding(@Param("topicId") UUID topicId,
                                @Param("fromExclusive") int fromExclusive,
                                @Param("toInclusive") int toInclusive,
                                @Param("excludeProblemId") UUID excludeProblemId);
}
