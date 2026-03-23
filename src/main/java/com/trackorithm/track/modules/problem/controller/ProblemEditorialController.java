package com.trackorithm.track.modules.problem.controller;

import com.trackorithm.track.modules.problem.dto.ProblemEditorialDto;
import com.trackorithm.track.modules.problem.service.ProblemEditorialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/problems")
public class ProblemEditorialController {
    private final ProblemEditorialService editorialService;

    public ProblemEditorialController(ProblemEditorialService editorialService) {
        this.editorialService = editorialService;
    }

    @GetMapping("/{problemId}/editorial")
    public ResponseEntity<ProblemEditorialDto> getEditorial(@PathVariable UUID problemId) {
        return editorialService.get(problemId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}

