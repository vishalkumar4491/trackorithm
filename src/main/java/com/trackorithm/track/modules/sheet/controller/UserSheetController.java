package com.trackorithm.track.modules.sheet.controller;

import com.trackorithm.track.modules.sheet.dto.CreateUserSheetRequest;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.dto.UpdateUserSheetRequest;
import com.trackorithm.track.modules.sheet.service.UserSheetService;
import com.trackorithm.track.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sheets/user")
@AllArgsConstructor
public class UserSheetController {
    private final UserSheetService userSheetService;
    private final CurrentUser currentUser;

    @PostMapping("/add")
    public SheetSummaryDto create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateUserSheetRequest request) {
        return userSheetService.create(currentUser.userId(jwt), request);
    }

    @PatchMapping("/{sheetId}")
    public SheetSummaryDto update(@AuthenticationPrincipal Jwt jwt,
                                  @PathVariable UUID sheetId,
                                  @Valid @RequestBody UpdateUserSheetRequest request) {
        return userSheetService.update(currentUser.userId(jwt), sheetId, request);
    }

    @DeleteMapping("/{sheetId}")
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID sheetId) {
        userSheetService.delete(currentUser.userId(jwt), sheetId);
    }
}

