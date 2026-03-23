package com.trackorithm.track.modules.problem.repo;

import com.trackorithm.track.modules.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProblemRepository extends JpaRepository<Problem, UUID> {
}

