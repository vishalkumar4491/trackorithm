package com.trackorithm.track.modules.sheet.controller;

import com.trackorithm.track.modules.sheet.dto.CreateSheetTagRequest;
import com.trackorithm.track.modules.sheet.dto.SheetTagDto;
import com.trackorithm.track.modules.sheet.service.SheetTagService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/sheet-tags")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminSheetTagController {
    private final SheetTagService sheetTagService;
    private final CurrentUser currentUser;

    @PostMapping
    public SheetTagDto create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateSheetTagRequest request) {
        return sheetTagService.createSystemTag(currentUser.userId(jwt), request);
    }
}

