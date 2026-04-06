package com.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import com.auth.util.DateTimeUtil;

import java.time.LocalDateTime;

/**
 * Base abstract entity with common fields and auditing metrics.
 */
@MappedSuperclass
@Data
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    /**
     * Executes on create logic.
     */

    @PrePersist
    protected void onCreate() {
        this.createdAt = DateTimeUtil.nowInIst();
        this.updatedAt = DateTimeUtil.nowInIst();
    }
    /**
     * Executes on update logic.
     */

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = DateTimeUtil.nowInIst();
    }
}
