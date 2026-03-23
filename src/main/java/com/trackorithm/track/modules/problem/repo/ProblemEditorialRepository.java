package com.trackorithm.track.modules.problem.repo;

import com.trackorithm.track.modules.problem.entity.ProblemEditorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProblemEditorialRepository extends JpaRepository<ProblemEditorial, UUID> {
    Optional<ProblemEditorial> findByProblem_Id(UUID problemId);
}
