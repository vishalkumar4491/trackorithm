package com.trackorithm.track.modules.notes.entity;

import com.trackorithm.track.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "note_versions")
@Getter
@Setter
public class NoteVersion extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Note note;

    @Column(columnDefinition = "TEXT")
    private String content;
}

