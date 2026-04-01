package com.trackorithm.track.modules.problem.service.impl;

import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.modules.notes.repo.NoteRepository;
import com.trackorithm.track.modules.problem.dto.ProblemDetailsDto;
import com.trackorithm.track.modules.problem.dto.ProblemEditorialDto;
import com.trackorithm.track.modules.problem.dto.ProblemLinkDto;
import com.trackorithm.track.modules.problem.dto.ProblemSummaryDto;
import com.trackorithm.track.modules.problem.mapper.ProblemLinkMapper;
import com.trackorithm.track.modules.problem.mapper.ProblemMapper;
import com.trackorithm.track.modules.problem.repo.ProblemEditorialRepository;
import com.trackorithm.track.modules.problem.repo.ProblemLinkRepository;
import com.trackorithm.track.modules.problem.repo.ProblemRepository;
import com.trackorithm.track.modules.problem.service.ProblemQueryService;
import com.trackorithm.track.modules.tracking.repo.UserProblemStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.trackorithm.track.modules.problem.enums.ProblemState.ACTIVE;

@Service
@AllArgsConstructor
public class ProblemQueryServiceImpl implements ProblemQueryService {
    private final ProblemRepository problemRepository;
    private final ProblemLinkRepository problemLinkRepository;
    private final ProblemEditorialRepository editorialRepository;
    private final UserProblemStatusRepository userProblemStatusRepository;
    private final NoteRepository noteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProblemSummaryDto> search(String q) {
        if (q == null || q.isBlank()) {
            return List.of();
        }
        return problemRepository.search(q.trim(), ACTIVE, PageRequest.of(0, 20)).stream()
                .map(ProblemMapper::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemDetailsDto getDetails(UUID userId, UUID problemId) {
        var problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new NotFoundException("Problem not found"));

        ProblemSummaryDto summary = ProblemMapper.toSummary(problem);

        List<ProblemLinkDto> links = problemLinkRepository.findByProblemId(problemId).stream()
                .map(ProblemLinkMapper::toDto)
                .toList();

        var editorial = editorialRepository.findByProblem_Id(problemId)
                .map(e -> new ProblemEditorialDto(
                        e.getProblem().getId(),
                        e.getContent(),
                        e.getYoutubeUrl(),
                        e.getReferenceUrl()
                ))
                .orElse(null);

        var ups = userProblemStatusRepository.findByUserIdAndProblemId(userId, problemId).orElse(null);
        boolean bookmarked = ups != null && Boolean.TRUE.equals(ups.getIsBookmarked());
        String status = ups != null && ups.getStatus() != null ? ups.getStatus().name() : "TODO";

        String note = noteRepository.findByUserIdAndProblemId(userId, problemId)
                .map(n -> n.getContent())
                .orElse(null);

        return new ProblemDetailsDto(summary, links, editorial, bookmarked, status, note);
    }
}
