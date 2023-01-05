/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

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

    var ownerRecordId: UUID,

    var firstName: String,

    var middleName: String,

    var lastName: String?,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    var birthDate: LocalDate,

    var job: String,

    var expectedSalary: String?,

    var about: String,

    var education: List<Education>,

    var professionalSkills: List<String>,

    var workHistory: List<WorkExperience>,

    var lastUpdate: LocalDateTime
)

data class Education(

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    var startTime: LocalDate,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    var endTime: LocalDate,

    var educationalInstitution: String,

    var educationLevel: String
)

class WorkExperience(

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    var startTime: LocalDate,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    var endTime: LocalDate,

    var companyName: String,

    var workingSpecialty: String,

    var responsibilities: List<String>,

    var tags: List<String>,
)

