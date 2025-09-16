package a.gleb.user_service.service;

import a.gleb.user_service.db.repository.OutboxMessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxMessageService {

    private final StreamBridge streamBridge;
    private final OutboxMessageRepository outboxMessageRepository;

    /**
     * Send message to queue, and update message status.
     */
    @Transactional
    public void proceed() {
        outboxMessageRepository.findAllBySent(false)
                .forEach(it -> {
                    try {
                        streamBridge.send(it.getBindingName(), it.getMessage());
                        it.setSent(Boolean.TRUE);
                        outboxMessageRepository.save(it);
                    } catch (Exception e) {
                        log.error("send outbox message error", e);
                    }
                });
    }
}
