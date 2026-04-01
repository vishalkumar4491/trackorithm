package com.trackorithm.track.modules.problem.repo;

import com.trackorithm.track.modules.problem.entity.SubtopicProblem;
import com.trackorithm.track.modules.problem.entity.SubtopicProblemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SubtopicProblemRepository extends JpaRepository<SubtopicProblem, SubtopicProblemId> {
    List<SubtopicProblem> findBySubtopic_IdOrderByOrderIndexAsc(UUID subtopicId);

    @Query("""
            select sp
            from SubtopicProblem sp
            join fetch sp.problem p
            where sp.subtopic.id in :subtopicIds
            order by sp.subtopic.id asc, sp.orderIndex asc
            """)
    List<SubtopicProblem> findBySubtopicIdsWithProblem(@Param("subtopicIds") List<UUID> subtopicIds);

    int countBySubtopic_Id(UUID subtopicId);

    java.util.Optional<SubtopicProblem> findFirstBySubtopic_Topic_Sheet_IdAndProblem_Id(UUID sheetId, UUID problemId);

    @Modifying
    @Query("""
            delete from SubtopicProblem sp
            where sp.subtopic.id = :subtopicId
              and sp.problem.id = :problemId
            """)
    int deleteBySubtopicAndProblem(@Param("subtopicId") UUID subtopicId, @Param("problemId") UUID problemId);

    @Modifying
    @Query("""
            update SubtopicProblem sp
            set sp.orderIndex = sp.orderIndex + 1
            where sp.subtopic.id = :subtopicId
              and sp.orderIndex >= :fromIndex
            """)
    int incrementFrom(@Param("subtopicId") UUID subtopicId, @Param("fromIndex") int fromIndex);

    @Modifying
    @Query("""
            update SubtopicProblem sp
            set sp.orderIndex = sp.orderIndex - 1
            where sp.subtopic.id = :subtopicId
              and sp.orderIndex > :afterIndex
            """)
    int decrementAfter(@Param("subtopicId") UUID subtopicId, @Param("afterIndex") int afterIndex);

    @Modifying
    @Query("""
            update SubtopicProblem sp
            set sp.orderIndex = sp.orderIndex + 1
            where sp.subtopic.id = :subtopicId
              and sp.orderIndex >= :fromInclusive
              and sp.orderIndex < :toExclusive
              and sp.problem.id <> :excludeProblemId
            """)
    int incrementRangeExcluding(@Param("subtopicId") UUID subtopicId,
                                @Param("fromInclusive") int fromInclusive,
                                @Param("toExclusive") int toExclusive,
                                @Param("excludeProblemId") UUID excludeProblemId);

    @Modifying
    @Query("""
            update SubtopicProblem sp
            set sp.orderIndex = sp.orderIndex - 1
            where sp.subtopic.id = :subtopicId
              and sp.orderIndex > :fromExclusive
              and sp.orderIndex <= :toInclusive
              and sp.problem.id <> :excludeProblemId
            """)
    int decrementRangeExcluding(@Param("subtopicId") UUID subtopicId,
                                @Param("fromExclusive") int fromExclusive,
                                @Param("toInclusive") int toInclusive,
                                @Param("excludeProblemId") UUID excludeProblemId);
}
