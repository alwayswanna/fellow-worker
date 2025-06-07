package a.gleb.oauth2server.service;

import a.gleb.oauth2server.db.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxMessageService {

    private static final String USER_REMOVE_BINDING_NAME = "remove-user-out-0";

    private final MessagePublisherService messagePublisherService;
    private final OutboxMessageRepository outboxMessageRepository;

    /**
     * Send message to queue, and update message status.
     */
    public void proceed() {
        outboxMessageRepository.findAllBySent( false)
                .forEach(it -> {
                    messagePublisherService.send(USER_REMOVE_BINDING_NAME, it);
                    it.setSent(Boolean.TRUE);
                    outboxMessageRepository.save(it);
                });
    }
}
