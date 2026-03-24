package com.trackorithm.track.modules.sheet.controller;

import com.trackorithm.track.modules.sheet.dto.CreateSheetTagRequest;
import com.trackorithm.track.modules.sheet.dto.SheetTagDto;
import com.trackorithm.track.modules.sheet.service.SheetTagService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sheet-tags")
@AllArgsConstructor
public class SheetTagController {
    private final SheetTagService sheetTagService;
    private final CurrentUser currentUser;

    @GetMapping
    public List<SheetTagDto> list(@AuthenticationPrincipal Jwt jwt) {
        return sheetTagService.listVisible(currentUser.userId(jwt));
    }

    @PostMapping
    public SheetTagDto create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateSheetTagRequest request) {
        return sheetTagService.createUserTag(currentUser.userId(jwt), request);
    }
}

