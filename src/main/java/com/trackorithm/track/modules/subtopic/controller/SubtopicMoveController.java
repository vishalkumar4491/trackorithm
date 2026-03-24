package com.trackorithm.track.modules.subtopic.controller;

import com.trackorithm.track.modules.subtopic.dto.MoveSubtopicRequest;
import com.trackorithm.track.modules.subtopic.dto.SubtopicDto;
import com.trackorithm.track.modules.subtopic.service.SubtopicService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sheets/{sheetId}/subtopics")
@AllArgsConstructor
public class SubtopicMoveController {
    private final SubtopicService subtopicService;
    private final CurrentUser currentUser;

    @PatchMapping("/{subtopicId}/move")
    public SubtopicDto move(@AuthenticationPrincipal Jwt jwt,
                            @PathVariable UUID sheetId,
                            @PathVariable UUID subtopicId,
                            @Valid @RequestBody MoveSubtopicRequest request) {
        return subtopicService.move(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, subtopicId, request);
    }
}

