package com.trackorithm.track.modules.notes.repo;

import com.trackorithm.track.modules.notes.entity.NoteVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NoteVersionRepository extends JpaRepository<NoteVersion, UUID> {
    List<NoteVersion> findTop20ByNoteIdOrderByCreatedAtDesc(UUID noteId);
}

