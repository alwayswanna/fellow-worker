package a.gleb.main_service.mapper.resume.sub

import a.gleb.main_service.db.entity.resume.sub.ResumeSkill
import org.springframework.stereotype.Component

@Component
class ResumeSkillMapper {

    fun toEntity(request: String): ResumeSkill {
        return ResumeSkill(
            skill = request,
        )
    }

    fun toResponse(skillEntities: MutableList<ResumeSkill>?): Set<String>? {
        if (skillEntities != null && skillEntities.isNotEmpty()) {
            return skillEntities.map { it.skill }.toSet()
        }

        return null;
    }
}