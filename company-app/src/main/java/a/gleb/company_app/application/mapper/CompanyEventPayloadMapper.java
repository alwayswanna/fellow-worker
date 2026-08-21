package a.gleb.company_app.application.mapper;

import a.gleb.company_app.domain.model.Company;
import a.gleb.fellow_worker.kafka.event.CompanyEventPayload;
import a.gleb.fellow_worker.kafka.event.CompanyEventType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CompanyEventPayloadMapper {

    public CompanyEventPayload toPayload(Company company, CompanyEventType eventType) {
        return new CompanyEventPayload(
                UUID.randomUUID(),
                eventType.name(),
                LocalDateTime.now(),
                company.getId()
        );
    }
}
