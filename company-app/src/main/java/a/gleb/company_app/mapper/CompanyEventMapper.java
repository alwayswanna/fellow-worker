package a.gleb.company_app.mapper;

import a.gleb.company_app.db.entity.CompanyEntity;
import a.gleb.fellow_worker.kafka.event.CompanyEventPayload;
import a.gleb.fellow_worker.kafka.event.CompanyEventType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CompanyEventMapper {

    public CompanyEventPayload toPayload(CompanyEntity company, CompanyEventType eventType) {
        return new CompanyEventPayload(
                UUID.randomUUID(),
                eventType.name(),
                LocalDateTime.now(),
                company.getId()
        );
    }
}
