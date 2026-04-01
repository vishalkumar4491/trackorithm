package com.trackorithm.track.modules.problem.service.impl;

import com.trackorithm.track.common.exception.ConflictException;
import com.trackorithm.track.common.exception.NotFoundException;
import com.trackorithm.track.modules.problem.dto.AdminProblemDto;
import com.trackorithm.track.modules.problem.dto.CreateProblemLinkRequest;
import com.trackorithm.track.modules.problem.dto.CreateProblemRequest;
import com.trackorithm.track.modules.problem.dto.ProblemLinkDto;
import com.trackorithm.track.modules.problem.dto.UpdateProblemRequest;
import com.trackorithm.track.modules.problem.entity.Problem;
import com.trackorithm.track.modules.problem.entity.ProblemLink;
import com.trackorithm.track.modules.problem.enums.ProblemState;
import com.trackorithm.track.modules.problem.mapper.ProblemLinkMapper;
import com.trackorithm.track.modules.problem.repo.ProblemLinkRepository;
import com.trackorithm.track.modules.problem.repo.ProblemRepository;
import com.trackorithm.track.modules.problem.service.AdminProblemCatalogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminProblemCatalogServiceImpl implements AdminProblemCatalogService {
    private final ProblemRepository problemRepository;
    private final ProblemLinkRepository problemLinkRepository;

    public AdminProblemCatalogServiceImpl(ProblemRepository problemRepository,
                                         ProblemLinkRepository problemLinkRepository) {
        this.problemRepository = problemRepository;
        this.problemLinkRepository = problemLinkRepository;
    }

    @Override
    @Transactional
    public AdminProblemDto create(UUID adminUserId, CreateProblemRequest request) {
        // adminUserId reserved for auditing later (created_by), kept in signature for consistency.
        String title = requireNonBlank(request.title(), "title");
        String slug = requireNonBlank(request.slug(), "slug");
        String canonicalUrl = requireNonBlank(request.canonicalUrl(), "canonicalUrl");
        String externalId = blankToNull(request.externalId());

        if (problemRepository.existsBySlugIgnoreCase(slug)) {
            throw new ConflictException("Problem slug already exists");
        }

        ensureLinkNotAlreadyUsed(request.platform(), canonicalUrl, externalId, null);

        Problem p = new Problem();
        p.setTitle(title);
        p.setSlug(slug);
        p.setPlatform(request.platform());
        p.setDifficulty(request.difficulty());
        p.setState(request.state() == null ? ProblemState.ACTIVE : request.state());
        p.setIsListed(request.listed() == null ? Boolean.FALSE : request.listed());
        // Backward-compatible fields (older DTOs still rely on these).
        p.setProblemUrl(canonicalUrl);
        p.setExternalProblemId(externalId);

        problemRepository.save(p);

        ProblemLink link = new ProblemLink();
        link.setProblem(p);
        link.setPlatform(request.platform());
        link.setCanonicalUrl(canonicalUrl);
        link.setExternalId(externalId);
        link.setTitleOnPlatform(title);
        link.setDifficultyOnPlatform(request.difficulty());
        problemLinkRepository.save(link);

        return toAdminDto(p, List.of(link));
    }

    @Override
    @Transactional
    public AdminProblemDto addLink(UUID adminUserId, UUID problemId, CreateProblemLinkRequest request) {
        // adminUserId reserved for auditing later (created_by), kept in signature for consistency.
        Problem p = problemRepository.findById(problemId)
                .orElseThrow(() -> new NotFoundException("Problem not found"));

        String canonicalUrl = requireNonBlank(request.canonicalUrl(), "canonicalUrl");
        String externalId = blankToNull(request.externalId());

        // If link already exists for this problem, return current view (idempotent).
        var existingByExternal = externalId == null ? null :
                problemLinkRepository.findFirstByPlatformAndExternalId(request.platform(), externalId).orElse(null);
        if (existingByExternal != null) {
            if (!existingByExternal.getProblem().getId().equals(problemId)) {
                throw new ConflictException("Problem link already belongs to another problem");
            }
            return toAdminDto(p, problemLinkRepository.findByProblemId(problemId));
        }

        var existingByUrl = problemLinkRepository.findFirstByPlatformAndCanonicalUrl(request.platform(), canonicalUrl).orElse(null);
        if (existingByUrl != null) {
            if (!existingByUrl.getProblem().getId().equals(problemId)) {
                throw new ConflictException("Problem link already belongs to another problem");
            }
            return toAdminDto(p, problemLinkRepository.findByProblemId(problemId));
        }

        ProblemLink link = new ProblemLink();
        link.setProblem(p);
        link.setPlatform(request.platform());
        link.setCanonicalUrl(canonicalUrl);
        link.setExternalId(externalId);
        link.setTitleOnPlatform(blankToNull(request.titleOnPlatform()));
        link.setDifficultyOnPlatform(request.difficultyOnPlatform());
        problemLinkRepository.save(link);

        return toAdminDto(p, problemLinkRepository.findByProblemId(problemId));
    }

    @Override
    @Transactional
    public AdminProblemDto update(UUID adminUserId, UUID problemId, UpdateProblemRequest request) {
        // adminUserId reserved for auditing later (updated_by), kept in signature for consistency.
        Problem p = problemRepository.findById(problemId)
                .orElseThrow(() -> new NotFoundException("Problem not found"));

        if (request.title() != null) {
            p.setTitle(requireNonBlank(request.title(), "title"));
        }
        if (request.slug() != null) {
            String newSlug = requireNonBlank(request.slug(), "slug");
            if (problemRepository.existsOtherBySlugIgnoreCase(newSlug, problemId)) {
                throw new ConflictException("Problem slug already exists");
            }
            p.setSlug(newSlug);
        }
        if (request.platform() != null) {
            p.setPlatform(request.platform());
        }
        if (request.difficulty() != null) {
            p.setDifficulty(request.difficulty());
        }
        if (request.state() != null) {
            p.setState(request.state());
        }
        if (request.listed() != null) {
            p.setIsListed(request.listed());
        }

        problemRepository.save(p);
        return toAdminDto(p, problemLinkRepository.findByProblemId(problemId));
    }

    private void ensureLinkNotAlreadyUsed(com.trackorithm.track.common.enums.Platform platform,
                                          String canonicalUrl,
                                          String externalId,
                                          UUID currentProblemId) {
        if (externalId != null) {
            problemLinkRepository.findFirstByPlatformAndExternalId(platform, externalId)
                    .ifPresent(existing -> {
                        if (currentProblemId == null || !existing.getProblem().getId().equals(currentProblemId)) {
                            throw new ConflictException("Problem link already exists (platform + externalId)");
                        }
                    });
        }
        problemLinkRepository.findFirstByPlatformAndCanonicalUrl(platform, canonicalUrl)
                .ifPresent(existing -> {
                    if (currentProblemId == null || !existing.getProblem().getId().equals(currentProblemId)) {
                        throw new ConflictException("Problem link already exists (platform + canonicalUrl)");
                    }
                });
    }

    private static AdminProblemDto toAdminDto(Problem p, List<ProblemLink> links) {
        List<ProblemLinkDto> linkDtos = links.stream().map(ProblemLinkMapper::toDto).toList();
        return new AdminProblemDto(
                p.getId(),
                p.getTitle(),
                p.getSlug(),
                p.getPlatform(),
                p.getDifficulty(),
                p.getState(),
                Boolean.TRUE.equals(p.getIsListed()),
                linkDtos
        );
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}

