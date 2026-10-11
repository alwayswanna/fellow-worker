package a.gleb.resume_app.adapter.out.security;

import a.gleb.resume_app.application.port.out.CurrentAccountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

/**
 * Feeds {@code @CreatedBy}/{@code @LastModifiedBy} ({@link a.gleb.resume_app.adapter.out.persistence.entity.BaseEntity})
 * from the authenticated caller. Returns empty rather than throwing when there's no authenticated
 * account (e.g. entity changes triggered from the Kafka listener in
 * {@link a.gleb.resume_app.adapter.in.messaging.ResumeAppKafkaConfiguration}, which has no JWT) -
 * auditing is best-effort there, not a hard requirement.
 */
@Component
@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements AuditorAware<UUID> {

    private final CurrentAccountPort currentAccountPort;

    @Override
    public Optional<UUID> getCurrentAuditor() {
        try {
            return Optional.of(currentAccountPort.requiredAccountId());
        } catch (ResponseStatusException e) {
            return Optional.empty();
        }
    }
}
