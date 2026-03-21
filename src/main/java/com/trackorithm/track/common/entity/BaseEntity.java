package com.trackorithm.track.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    @Column(name = "created_at",  updatable = false, nullable = false)
    private LocalDateTime createdAt =  LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt =  LocalDateTime.now();

    @PreUpdate
    public void prePersist() {
        updatedAt = LocalDateTime.now();
    }
}
