package com.trackorithm.track.modules.problem.controller;

import com.trackorithm.track.modules.problem.dto.AdminProblemDto;
import com.trackorithm.track.modules.problem.dto.CreateProblemLinkRequest;
import com.trackorithm.track.modules.problem.dto.CreateProblemRequest;
import com.trackorithm.track.modules.problem.dto.UpdateProblemRequest;
import com.trackorithm.track.modules.problem.service.AdminProblemCatalogService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/problems")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminProblemCatalogController {
    private final AdminProblemCatalogService adminProblemCatalogService;
    private final CurrentUser currentUser;

    @PostMapping
    public AdminProblemDto create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateProblemRequest request) {
        return adminProblemCatalogService.create(currentUser.userId(jwt), request);
    }

    @PostMapping("/{problemId}/links")
    public AdminProblemDto addLink(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable UUID problemId,
                                   @Valid @RequestBody CreateProblemLinkRequest request) {
        return adminProblemCatalogService.addLink(currentUser.userId(jwt), problemId, request);
    }

    @PatchMapping("/{problemId}")
    public AdminProblemDto update(@AuthenticationPrincipal Jwt jwt,
                                  @PathVariable UUID problemId,
                                  @Valid @RequestBody UpdateProblemRequest request) {
        return adminProblemCatalogService.update(currentUser.userId(jwt), problemId, request);
    }
}

