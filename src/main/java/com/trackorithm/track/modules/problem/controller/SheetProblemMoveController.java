package com.trackorithm.track.modules.problem.controller;

import com.trackorithm.track.modules.problem.dto.MoveProblemRequest;
import com.trackorithm.track.modules.problem.service.ProblemPlacementService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sheets/{sheetId}/problems")
public class SheetProblemMoveController {
    private final ProblemPlacementService placementService;
    private final CurrentUser currentUser;

    public SheetProblemMoveController(ProblemPlacementService placementService, CurrentUser currentUser) {
        this.placementService = placementService;
        this.currentUser = currentUser;
    }

    @PatchMapping("/{problemId}/move")
    public void move(@AuthenticationPrincipal Jwt jwt,
                     @PathVariable UUID sheetId,
                     @PathVariable UUID problemId,
                     @Valid @RequestBody MoveProblemRequest request) {
        placementService.moveWithinSheet(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, problemId, request);
    }
}

