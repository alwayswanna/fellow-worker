package a.gleb.main_service.db.repository

import a.gleb.main_service.db.entity.vacancy.VacancyEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface VacancyEntityRepository : ReactiveCrudRepository<VacancyEntity, UUID> {
}