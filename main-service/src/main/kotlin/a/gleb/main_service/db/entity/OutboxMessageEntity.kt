package a.gleb.main_service.db.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "outbox_messages")
class OutboxMessageEntity(

    @Column(value = "message")
    var message: String,

    @Column(value = "binding_name")
    var bindingName: String,

    @Column(value = "is_sent")
    var sent: Boolean
): BaseEntity() {
}