package a.gleb.main_service.db.entity.resume.sub

import a.gleb.main_service.db.entity.BaseEntity
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table(name = "resume_skills")
class ResumeSkill(

    @Column("resume_id")
    var resumeId: UUID? = null,

    @Column("skill")
    var skill: String

) : BaseEntity() {
}