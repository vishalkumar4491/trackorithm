package com.trackorithm.track.modules.sheet.controller;

import com.trackorithm.track.common.web.PageResponse;
import com.trackorithm.track.modules.sheet.dto.SheetSummaryDto;
import com.trackorithm.track.modules.sheet.service.SheetCatalogService;
import com.trackorithm.track.modules.sheet.service.UserSheetService;
import com.trackorithm.track.security.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/sheets")
@AllArgsConstructor
public class MySheetController {
    private final SheetCatalogService sheetCatalogService;
    private final UserSheetService userSheetService;
    private final CurrentUser currentUser;

    @GetMapping("/system")
    public PageResponse<SheetSummaryDto> myEnrolledSystemSheets(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        return sheetCatalogService.listMyEnrolledSystemSheets(currentUser.userId(jwt), pageable);
    }

    @GetMapping("/user")
    public PageResponse<SheetSummaryDto> myUserPersonalSheets(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        return userSheetService.listMyPersoanlSheets(currentUser.userId(jwt), pageable);
    }
}

