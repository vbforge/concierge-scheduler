package com.vbforge.concierge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base entity class with audit fields
 * All entities should extend this class
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable{

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Soft delete flag - instead of physically deleting records
     */
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    /**
     * Hook called before entity is persisted
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Hook called before entity is updated
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Soft delete method
     */
    public void softDelete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Restore soft deleted entity
     */
    public void restore() {
        this.deleted = false;
        this.updatedAt = LocalDateTime.now();
    }

}
