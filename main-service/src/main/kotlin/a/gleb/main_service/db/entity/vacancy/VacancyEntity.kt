package a.gleb.main_service.db.entity.vacancy

import a.gleb.main_service.db.entity.BaseEntity
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table(name = "vacancies")
class VacancyEntity(
    @Column(value= "title")
    var title: String,

    @Column(value= "description")
    var description: String,

    @Column(value= "company_name")
    var companyName: String,

    @Column(value= "contact_email")
    var contactEmail: String? = null,

    @Column(value= "salary_from")
    var salaryFrom: Int? = null,

    @Column(value= "salary_to")
    var salaryTo: Int? = null,

    @Column(value= "salary_currency")
    var salaryCurrency: String? = null,

    var requirements: MutableSet<String> = mutableSetOf(),

    var benefits: MutableSet<String> = mutableSetOf(),

    @Column(value= "employment_type")
    var employmentType: String? = null,

    @Column(value= "work_schedule")
    var workSchedule: String? = null,

    @Column(value= "experience_level")
    var experienceLevel: String? = null,

    @Column(value= "location")
    var location: String? = null,

    @Column(value= "is_remote")
    var isRemote: Boolean = false,

    @Column(value= "is_active")
    var isActive: Boolean = true,

    @Column(value= "application_deadline")
    var applicationDeadline: LocalDate? = null

) : BaseEntity()