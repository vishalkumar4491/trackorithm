package com.trackorithm.track.modules.topic.controller;

import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.topic.dto.CreateTopicRequest;
import com.trackorithm.track.modules.topic.dto.ReorderTopicsRequest;
import com.trackorithm.track.modules.topic.dto.TopicDto;
import com.trackorithm.track.modules.topic.dto.UpdateTopicRequest;
import com.trackorithm.track.modules.topic.service.TopicService;
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
@RequestMapping("/api/sheets/{sheetId}/topics")
@AllArgsConstructor
public class TopicController {
    private final TopicService topicService;
    private final CurrentUser currentUser;

    @GetMapping
    public PageResponse<TopicDto> list(@AuthenticationPrincipal Jwt jwt,
                                       @PathVariable UUID sheetId,
                                       Pageable pageable) {
        return topicService.list(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, pageable);
    }

    @PostMapping
    public TopicDto create(@AuthenticationPrincipal Jwt jwt,
                           @PathVariable UUID sheetId,
                           @Valid @RequestBody CreateTopicRequest request) {
        return topicService.create(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, request);
    }

    @PatchMapping("/{topicId}")
    public TopicDto update(@AuthenticationPrincipal Jwt jwt,
                           @PathVariable UUID sheetId,
                           @PathVariable UUID topicId,
                           @Valid @RequestBody UpdateTopicRequest request) {
        return topicService.update(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, topicId, request);
    }

    @DeleteMapping("/{topicId}")
    public void delete(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable UUID sheetId,
                       @PathVariable UUID topicId) {
        topicService.delete(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, topicId);
    }

    @PutMapping("/reorder")
    public List<TopicDto> reorder(@AuthenticationPrincipal Jwt jwt,
                                  @PathVariable UUID sheetId,
                                  @Valid @RequestBody ReorderTopicsRequest request) {
        return topicService.reorder(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, request);
    }
}

