/*
 * Copyright (c) 12-2/25/23, 11:54 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.db.dao

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
data class Vacancy(

    @field:Id
    var id: UUID,

    var ownerId: UUID,

    var vacancyName: String,

    var salary: String?,

    var typeWork: TypeOfWork,

    var typeOfWorkPlacement: TypePlacement,

    var companyName: String,

    var companyFullAddress: String,

    var keySkills: List<String>,

    var cityName: String,

    var workingResponsibilities: List<String>,

    var companyBonuses: List<String>,

    var companyContact: VacancyContactInfo,

    var lastUpdate: LocalDateTime
)

enum class TypeOfWork {

    FULL_EMPLOYMENT,
    PART_TIME_EMPLOYMENT
}

enum class TypePlacement {

    OFFICE,
    REMOTE
}

data class VacancyContactInfo(

    var fio: String,

    var phone: String,

    var email: String
)