package com.trackorithm.track.modules.notes.repo;

import com.trackorithm.track.modules.notes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    Optional<Note> findByUserIdAndProblemId(UUID userId, UUID problemId);
}

