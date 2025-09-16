package a.gleb.main_service.db.entity.resume.sub

import a.gleb.main_service.db.entity.BaseEntity
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.*

@Table(name = "resume_educations")
class ResumeEducation(

    @Column(value = "resume_id")
    var resumeId: UUID?,

    @Column(value = "institution")
    var institution: String,

    @Column(value = "degree")
    var degree: String,

    @Column(value = "field_of_study")
    var fieldOfStudy: String,

    @Column(value = "start_date")
    var startDate: LocalDate,

    @Column(value = "end_date")
    var endDate: LocalDate? = null,

    @Column(value = "is_current")
    var isCurrent: Boolean = false,

    @Column(value = "description")
    var description: String? = null
) : BaseEntity() {}