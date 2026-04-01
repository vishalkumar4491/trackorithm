package com.trackorithm.track.modules.problem.controller;

import com.trackorithm.track.modules.problem.dto.ProblemDetailsDto;
import com.trackorithm.track.modules.problem.dto.SetBookmarkRequest;
import com.trackorithm.track.modules.problem.dto.UpsertNoteRequest;
import com.trackorithm.track.modules.problem.service.ProblemOverlayService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/problems/{problemId}")
@AllArgsConstructor
public class ProblemOverlayController {
    private final ProblemOverlayService overlayService;
    private final CurrentUser currentUser;

    @PutMapping("/bookmark")
    public ProblemDetailsDto bookmark(@AuthenticationPrincipal Jwt jwt,
                                      @PathVariable UUID problemId,
                                      @Valid @RequestBody SetBookmarkRequest request) {
        return overlayService.setBookmark(currentUser.userId(jwt), problemId, request.bookmarked());
    }

    @PutMapping("/note")
    public ProblemDetailsDto note(@AuthenticationPrincipal Jwt jwt,
                                  @PathVariable UUID problemId,
                                  @Valid @RequestBody UpsertNoteRequest request) {
        return overlayService.upsertNote(currentUser.userId(jwt), problemId, request.content());
    }
}

