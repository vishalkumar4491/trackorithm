package com.trackorithm.track.modules.sheet.controller;

import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.dto.EnrollmentStatusDto;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.service.EnrollmentService;
import com.trackorithm.track.modules.sheet.service.SheetCatalogService;
import com.trackorithm.track.security.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sheets/system")
@AllArgsConstructor
public class SystemSheetController {
    private final SheetCatalogService sheetCatalogService;
    private final EnrollmentService enrollmentService;
    private final CurrentUser currentUser;

    @GetMapping
    public PageResponse<SheetSummaryDto> list(Pageable pageable) {
        return sheetCatalogService.listSystemSheets(pageable);
    }

    @GetMapping("/{sheetId}")
    public SheetSummaryDto get(@PathVariable UUID sheetId) {
        return sheetCatalogService.getSystemSheet(sheetId);
    }

    @GetMapping("/{sheetId}/enrollment")
    public EnrollmentStatusDto enrollment(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID sheetId) {
        UUID userId = currentUser.userId(jwt);
        return enrollmentService.enrollmentStatus(userId, sheetId);
    }

    @PostMapping("/{sheetId}/enroll")
    public EnrollmentStatusDto enroll(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID sheetId) {
        UUID userId = currentUser.userId(jwt);
        return enrollmentService.enroll(userId, sheetId);
    }

    @DeleteMapping("/{sheetId}/unenroll")
    public EnrollmentStatusDto unenroll(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID sheetId) {
        UUID userId = currentUser.userId(jwt);
        return enrollmentService.unenroll(userId, sheetId);
    }
}

