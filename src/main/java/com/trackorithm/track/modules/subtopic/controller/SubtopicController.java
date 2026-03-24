package com.trackorithm.track.modules.subtopic.controller;

import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.subtopic.dto.*;
import com.trackorithm.track.modules.subtopic.service.SubtopicService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sheets/{sheetId}/topics/{topicId}/subtopics")
@AllArgsConstructor
public class SubtopicController {
    private final SubtopicService subtopicService;
    private final CurrentUser currentUser;

    @GetMapping
    public PageResponse<SubtopicDto> list(@AuthenticationPrincipal Jwt jwt,
                                          @PathVariable UUID sheetId,
                                          @PathVariable UUID topicId,
                                          Pageable pageable) {
        return subtopicService.list(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, topicId, pageable);
    }

    @PostMapping
    public SubtopicDto create(@AuthenticationPrincipal Jwt jwt,
                              @PathVariable UUID sheetId,
                              @PathVariable UUID topicId,
                              @Valid @RequestBody CreateSubtopicRequest request) {
        return subtopicService.create(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, topicId, request);
    }

    @PatchMapping("/{subtopicId}")
    public SubtopicDto update(@AuthenticationPrincipal Jwt jwt,
                              @PathVariable UUID sheetId,
                              @PathVariable UUID topicId,
                              @PathVariable UUID subtopicId,
                              @Valid @RequestBody UpdateSubtopicRequest request) {
        return subtopicService.update(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, topicId, subtopicId, request);
    }

    @DeleteMapping("/{subtopicId}")
    public void delete(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable UUID sheetId,
                       @PathVariable UUID topicId,
                       @PathVariable UUID subtopicId) {
        subtopicService.delete(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, topicId, subtopicId);
    }

    @PutMapping("/reorder")
    public List<SubtopicDto> reorder(@AuthenticationPrincipal Jwt jwt,
                                     @PathVariable UUID sheetId,
                                     @PathVariable UUID topicId,
                                     @Valid @RequestBody ReorderSubtopicsRequest request) {
        return subtopicService.reorder(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, topicId, request);
    }
}

