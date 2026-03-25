package com.trackorithm.track.modules.sheet.controller;

import com.trackorithm.track.modules.sheet.dto.SheetHierarchyDto;
import com.trackorithm.track.modules.sheet.service.SheetHierarchyService;
import com.trackorithm.track.modules.subtopic.dto.SubtopicHierarchyDto;
import com.trackorithm.track.modules.topic.dto.TopicHierarchyDto;
import com.trackorithm.track.security.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sheets/{sheetId}")
@AllArgsConstructor
public class SheetHierarchyController {
    private final SheetHierarchyService sheetHierarchyService;
    private final CurrentUser currentUser;

    @GetMapping("/hierarchy")
    public SheetHierarchyDto sheetHierarchy(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID sheetId) {
        return sheetHierarchyService.sheetHierarchy(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId);
    }

    @GetMapping("/topics/{topicId}/hierarchy")
    public TopicHierarchyDto topicHierarchy(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable UUID sheetId,
                                            @PathVariable UUID topicId) {
        return sheetHierarchyService.topicHierarchy(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, topicId);
    }

    @GetMapping("/topics/{topicId}/subtopics/{subtopicId}/hierarchy")
    public SubtopicHierarchyDto subtopicHierarchy(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable UUID sheetId,
                                                  @PathVariable UUID topicId,
                                                  @PathVariable UUID subtopicId) {
        return sheetHierarchyService.subtopicHierarchy(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, topicId, subtopicId);
    }
}

