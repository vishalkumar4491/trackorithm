package com.trackorithm.track.modules.problem.controller;

import com.trackorithm.track.modules.problem.dto.ProblemEditorialDto;
import com.trackorithm.track.modules.problem.dto.UpsertProblemEditorialRequest;
import com.trackorithm.track.modules.problem.service.ProblemEditorialService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/problems")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProblemEditorialController {
    private final ProblemEditorialService editorialService;
    private final CurrentUser currentUser;

    public AdminProblemEditorialController(ProblemEditorialService editorialService, CurrentUser currentUser) {
        this.editorialService = editorialService;
        this.currentUser = currentUser;
    }

    @PutMapping("/{problemId}/editorial")
    public ResponseEntity<ProblemEditorialDto> upsert(@AuthenticationPrincipal Jwt jwt,
                                                      @PathVariable UUID problemId,
                                                      @Valid @RequestBody UpsertProblemEditorialRequest request) {
        UUID adminUserId = currentUser.userId(jwt);
        return editorialService.upsert(adminUserId, problemId, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}

