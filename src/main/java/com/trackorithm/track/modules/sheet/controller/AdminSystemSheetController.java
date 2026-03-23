package com.trackorithm.track.modules.sheet.controller;

import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.dto.CreateSystemSheetRequest;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.dto.UpdateSystemSheetRequest;
import com.trackorithm.track.modules.sheet.service.AdminSystemSheetService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/sheets/system")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminSystemSheetController {
    private final AdminSystemSheetService adminSystemSheetService;
    private final CurrentUser currentUser;

    @GetMapping("/sheets")
    public PageResponse<SheetSummaryDto> getAllSheets(Pageable pageable) {
        return adminSystemSheetService.listAll(pageable);
    }

    @PostMapping("/sheet")
    public SheetSummaryDto addSheet(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateSystemSheetRequest request) {
        return adminSystemSheetService.create(currentUser.userId(jwt), request);
    }

    @PatchMapping("/sheet/{sheetId}")
    public SheetSummaryDto update(@AuthenticationPrincipal Jwt jwt,
                                  @PathVariable UUID sheetId,
                                  @Valid @RequestBody UpdateSystemSheetRequest request) {
        return adminSystemSheetService.update(currentUser.userId(jwt), sheetId, request);
    }

    @DeleteMapping("/sheet/{sheetId}")
    public void delete(@PathVariable UUID sheetId) {
        adminSystemSheetService.delete(sheetId);
    }
}

