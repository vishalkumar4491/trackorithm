package com.trackorithm.track.modules.problem.service.impl;

import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.modules.notes.entity.Note;
import com.trackorithm.track.modules.notes.entity.NoteVersion;
import com.trackorithm.track.modules.notes.repo.NoteRepository;
import com.trackorithm.track.modules.notes.repo.NoteVersionRepository;
import com.trackorithm.track.modules.problem.dto.ProblemDetailsDto;
import com.trackorithm.track.modules.problem.repo.ProblemRepository;
import com.trackorithm.track.modules.problem.service.ProblemOverlayService;
import com.trackorithm.track.modules.problem.service.ProblemQueryService;
import com.trackorithm.track.modules.tracking.entity.UserProblemStatus;
import com.trackorithm.track.modules.tracking.repo.UserProblemStatusRepository;
import com.trackorithm.track.modules.user.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ProblemOverlayServiceImpl implements ProblemOverlayService {
    private final UserProblemStatusRepository userProblemStatusRepository;
    private final NoteRepository noteRepository;
    private final NoteVersionRepository noteVersionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final ProblemQueryService problemQueryService;

    @Override
    @Transactional
    public ProblemDetailsDto setBookmark(UUID userId, UUID problemId, boolean bookmarked) {
        if (!problemRepository.existsById(problemId)) {
            throw new NotFoundException("Problem not found");
        }

        UserProblemStatus ups = userProblemStatusRepository.findByUserIdAndProblemId(userId, problemId)
                .orElseGet(() -> {
                    UserProblemStatus s = new UserProblemStatus();
                    s.setUser(userRepository.getReferenceById(userId));
                    s.setProblem(problemRepository.getReferenceById(problemId));
                    return s;
                });

        ups.setIsBookmarked(bookmarked);
        userProblemStatusRepository.save(ups);
        return problemQueryService.getDetails(userId, problemId);
    }

    @Override
    @Transactional
    public ProblemDetailsDto upsertNote(UUID userId, UUID problemId, String content) {
        if (!problemRepository.existsById(problemId)) {
            throw new NotFoundException("Problem not found");
        }

        Note note = noteRepository.findByUserIdAndProblemId(userId, problemId)
                .orElseGet(() -> {
                    Note n = new Note();
                    n.setUser(userRepository.getReferenceById(userId));
                    n.setProblem(problemRepository.getReferenceById(problemId));
                    return n;
                });

        String normalized = content == null ? null : content;
        note.setContent(normalized);
        noteRepository.save(note);

        NoteVersion version = new NoteVersion();
        version.setNote(note);
        version.setContent(normalized == null ? "" : normalized);
        noteVersionRepository.save(version);

        return problemQueryService.getDetails(userId, problemId);
    }
}

