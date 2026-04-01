package com.trackorithm.track.modules.tracking.repo;

import com.trackorithm.track.modules.tracking.entity.UserProblemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProblemStatusRepository extends JpaRepository<UserProblemStatus, UUID> {
    Optional<UserProblemStatus> findByUserIdAndProblemId(UUID userId, UUID problemId);

    List<UserProblemStatus> findByUserIdAndProblemIdIn(UUID userId, List<UUID> problemIds);
}

