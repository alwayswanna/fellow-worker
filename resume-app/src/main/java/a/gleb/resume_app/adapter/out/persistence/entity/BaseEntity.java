package a.gleb.resume_app.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * {@code createdAt}/{@code updatedAt}/{@code createdBy}/{@code updatedBy} are populated by
 * {@link AuditingEntityListener} (see {@code @EnableJpaAuditing} in {@code ResumeAppConfig} and
 * {@link a.gleb.resume_app.adapter.out.security.SpringSecurityAuditorAware}) rather than manual
 * {@code @PrePersist}/{@code @PreUpdate} callbacks.
 */
@Getter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;

    /** Optimistic locking — lets concurrent PUTs on the same resume fail with a 409 instead of silently clobbering each other. */
    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
