package com.trackorithm.track.modules.problem.controller;

import com.trackorithm.track.modules.problem.dto.ProblemDetailsDto;
import com.trackorithm.track.modules.problem.dto.ProblemSummaryDto;
import com.trackorithm.track.modules.problem.service.ProblemQueryService;
import com.trackorithm.track.security.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/problems")
@AllArgsConstructor
public class ProblemController {
    private final ProblemQueryService problemQueryService;
    private final CurrentUser currentUser;

    @GetMapping("/search")
    public List<ProblemSummaryDto> search(@RequestParam(name = "q", required = false) String q) {
        return problemQueryService.search(q);
    }

    @GetMapping("/{problemId}")
    public ProblemDetailsDto details(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID problemId) {
        return problemQueryService.getDetails(currentUser.userId(jwt), problemId);
    }
}

