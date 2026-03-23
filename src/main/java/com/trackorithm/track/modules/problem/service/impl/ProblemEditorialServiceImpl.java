package com.trackorithm.track.modules.problem.service.impl;

import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.modules.problem.dto.ProblemEditorialDto;
import com.trackorithm.track.modules.problem.dto.UpsertProblemEditorialRequest;
import com.trackorithm.track.modules.problem.entity.Problem;
import com.trackorithm.track.modules.problem.entity.ProblemEditorial;
import com.trackorithm.track.modules.problem.mapper.ProblemEditorialMapper;
import com.trackorithm.track.modules.problem.repo.ProblemEditorialRepository;
import com.trackorithm.track.modules.problem.repo.ProblemRepository;
import com.trackorithm.track.modules.problem.service.ProblemEditorialService;
import com.trackorithm.track.modules.user.entity.User;
import com.trackorithm.track.modules.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProblemEditorialServiceImpl implements ProblemEditorialService {
    private final ProblemEditorialRepository editorialRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    public ProblemEditorialServiceImpl(ProblemEditorialRepository editorialRepository,
                                      ProblemRepository problemRepository,
                                      UserRepository userRepository) {
        this.editorialRepository = editorialRepository;
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProblemEditorialDto> get(UUID problemId) {
        return editorialRepository.findByProblem_Id(problemId).map(ProblemEditorialMapper::toDto);
    }

    @Override
    @Transactional
    public Optional<ProblemEditorialDto> upsert(UUID adminUserId, UUID problemId, UpsertProblemEditorialRequest request) {
        if (!problemRepository.existsById(problemId)) {
            throw new NotFoundException("Problem not found");
        }

        boolean allBlank = isBlank(request.content()) && isBlank(request.youtubeUrl()) && isBlank(request.referenceUrl());
        Optional<ProblemEditorial> existing = editorialRepository.findByProblem_Id(problemId);
        if (allBlank) {
            existing.ifPresent(editorialRepository::delete);
            return Optional.empty();
        }

        ProblemEditorial editorial = existing.orElseGet(ProblemEditorial::new);
        Problem problemRef = problemRepository.getReferenceById(problemId);
        User adminRef = userRepository.getReferenceById(adminUserId);

        editorial.setProblem(problemRef);
        if (editorial.getCreatedBy() == null) {
            editorial.setCreatedBy(adminRef);
        }
        editorial.setUpdatedBy(adminRef);
        editorial.setContent(blankToNull(request.content()));
        editorial.setYoutubeUrl(blankToNull(request.youtubeUrl()));
        editorial.setReferenceUrl(blankToNull(request.referenceUrl()));

        editorialRepository.save(editorial);
        return Optional.of(ProblemEditorialMapper.toDto(editorial));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String blankToNull(String value) {
        return isBlank(value) ? null : value;
    }
}

