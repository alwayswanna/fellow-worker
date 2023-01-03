package a.gleb.fellowworkerservice.db.dao

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Document
data class Resume(

    @field:Id
    var id: UUID,

    val ownerRecordId: UUID,

    val firstName: String,

    val middleName: String,

    val lastName: String?,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val birthDate: LocalDate,

    val job: String,

    val expectedSalary: String?,

    val about: String,

    val education: List<Education>,

    val professionalSkills: List<String>,

    val workHistory: List<WorkExperience>,

    val lastUpdate: LocalDateTime
)

data class Education(

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val startTime: LocalDate,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val endTime: LocalDate,

    val educationalInstitution: String,

    val educationLevel: String
)

class WorkExperience(

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val startTime: LocalDate,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    val endTime: LocalDate,

    val companyName: String,

    val workingSpecialty: String,

    val responsibilities: List<String>,

    val tags: List<String>,
)

