package com.trackorithm.track.modules.sheet.controller;

import com.trackorithm.track.modules.sheet.dto.AddSheetTagRequest;
import com.trackorithm.track.modules.sheet.dto.SheetTagDto;
import com.trackorithm.track.modules.sheet.service.SheetTagAssignmentService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sheets/{sheetId}/tags")
@AllArgsConstructor
public class SheetTagAssignmentController {
    private final SheetTagAssignmentService sheetTagAssignmentService;
    private final CurrentUser currentUser;

    @GetMapping
    public List<SheetTagDto> list(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID sheetId) {
        return sheetTagAssignmentService.listSheetTags(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId);
    }

    @PostMapping
    public List<SheetTagDto> add(@AuthenticationPrincipal Jwt jwt,
                                 @PathVariable UUID sheetId,
                                 @Valid @RequestBody AddSheetTagRequest request) {
        return sheetTagAssignmentService.addTagToSheet(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, request);
    }

    @DeleteMapping("/{tagId}")
    public List<SheetTagDto> remove(@AuthenticationPrincipal Jwt jwt,
                                    @PathVariable UUID sheetId,
                                    @PathVariable UUID tagId) {
        return sheetTagAssignmentService.removeTagFromSheet(currentUser.userId(jwt), currentUser.isAdmin(jwt), sheetId, tagId);
    }
}

