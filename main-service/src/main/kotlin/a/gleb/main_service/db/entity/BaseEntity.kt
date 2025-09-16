package a.gleb.main_service.db.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime
import java.util.*

abstract class BaseEntity(
    @Id
    var id: UUID? = null,
    @Column(value = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(value = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
}