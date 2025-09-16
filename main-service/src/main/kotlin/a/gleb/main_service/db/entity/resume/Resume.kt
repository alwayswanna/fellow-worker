package a.gleb.main_service.db.entity.resume

import a.gleb.main_service.db.entity.BaseEntity
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "resumes")
class Resume(
    @Column(value = "title")
    var title: String,

    @Column(value = "description")
    var description: String,

    @Column(value = "full_name")
    var fullName: String,

    @Column(value = "email")
    var email: String,

    @Column(value = "phone")
    var phone: String? = null,

    @Column(value = "salary_expectation")
    var salaryExpectation: Int? = null,

    @Column(value = "expected_salary_currency")
    var expectedSalaryCurrency: String? = null,

    @Column(value = "experience_years")
    var experienceYears: Int? = null,

    @Column(value = "employment_type")
    var employmentType: String? = null,

    @Column(value = "work_schedule")
    var workSchedule: String? = null,

    @Column(value = "is_active")
    var isActive: Boolean = true,

    @Column(value = "location")
    var location: String? = null,

    @Column(value = "languages")
    var languages: String? = null

) : BaseEntity()