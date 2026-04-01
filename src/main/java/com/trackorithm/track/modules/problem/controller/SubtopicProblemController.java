package com.trackorithm.track.modules.problem.controller;

import com.trackorithm.track.modules.problem.dto.AddProblemRequest;
import com.trackorithm.track.modules.problem.dto.ReorderProblemsRequest;
import com.trackorithm.track.modules.problem.service.ProblemPlacementService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sheets/{sheetId}/topics/{topicId}/subtopics/{subtopicId}/problems")
@AllArgsConstructor
public class SubtopicProblemController {
    private final ProblemPlacementService placementService;
    private final CurrentUser currentUser;

    @PostMapping
    public List<UUID> add(@AuthenticationPrincipal Jwt jwt,
                          @PathVariable UUID sheetId,
                          @PathVariable UUID topicId,
                          @PathVariable UUID subtopicId,
                          @Valid @RequestBody AddProblemRequest request) {
        return placementService.addToSubtopic(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, topicId, subtopicId, request);
    }

    @PutMapping("/reorder")
    public List<UUID> reorder(@AuthenticationPrincipal Jwt jwt,
                              @PathVariable UUID sheetId,
                              @PathVariable UUID topicId,
                              @PathVariable UUID subtopicId,
                              @Valid @RequestBody ReorderProblemsRequest request) {
        return placementService.reorderSubtopicProblems(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, topicId, subtopicId, request);
    }
}

