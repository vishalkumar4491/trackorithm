package com.trackorithm.track.modules.problem.repo;

import com.trackorithm.track.modules.problem.entity.Problem;
import com.trackorithm.track.modules.problem.enums.ProblemState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProblemRepository extends JpaRepository<Problem, UUID> {

    boolean existsBySlugIgnoreCase(String slug);

    @Query("""
            select (count(p) > 0)
            from Problem p
            where lower(p.slug) = lower(:slug)
              and p.id <> :excludeId
            """)
    boolean existsOtherBySlugIgnoreCase(@Param("slug") String slug, @Param("excludeId") UUID excludeId);

    @Query("""
            select p
            from Problem p
            where p.state = :state
              and (lower(p.title) like lower(concat('%', :q, '%'))
                   or lower(p.slug) like lower(concat('%', :q, '%')))
            order by p.isListed desc, p.title asc
            """)
    List<Problem> search(@Param("q") String q, @Param("state") ProblemState state, Pageable pageable);
}

