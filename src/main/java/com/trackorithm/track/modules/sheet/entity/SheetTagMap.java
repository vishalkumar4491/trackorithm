package com.trackorithm.track.modules.sheet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sheet_tag_map",
        indexes = {
                @Index(name = "idx_sheet_tag_map_sheet", columnList = "sheet_id"),
                @Index(name = "idx_sheet_tag_map_tag", columnList = "tag_id")
        })
@Getter
@Setter
public class SheetTagMap {

    @EmbeddedId
    private SheetTagMapId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sheetId")
    @JoinColumn(name = "sheet_id", nullable = false)
    private Sheet sheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id", nullable = false)
    private SheetTag tag;
}

