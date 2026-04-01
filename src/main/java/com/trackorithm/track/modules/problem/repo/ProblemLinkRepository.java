package com.trackorithm.track.modules.problem.repo;

import com.trackorithm.track.common.enums.Platform;
import com.trackorithm.track.modules.problem.entity.ProblemLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProblemLinkRepository extends JpaRepository<ProblemLink, UUID> {
    List<ProblemLink> findByProblemId(UUID problemId);

    Optional<ProblemLink> findFirstByPlatformAndExternalId(Platform platform, String externalId);

    Optional<ProblemLink> findFirstByPlatformAndCanonicalUrl(Platform platform, String canonicalUrl);
}

