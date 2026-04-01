package com.trackorithm.track.modules.problem.service;

import com.trackorithm.track.modules.problem.dto.AdminProblemDto;
import com.trackorithm.track.modules.problem.dto.CreateProblemLinkRequest;
import com.trackorithm.track.modules.problem.dto.CreateProblemRequest;
import com.trackorithm.track.modules.problem.dto.UpdateProblemRequest;

import java.util.UUID;

public interface AdminProblemCatalogService {
    AdminProblemDto create(UUID adminUserId, CreateProblemRequest request);

    AdminProblemDto addLink(UUID adminUserId, UUID problemId, CreateProblemLinkRequest request);

    AdminProblemDto update(UUID adminUserId, UUID problemId, UpdateProblemRequest request);
}

