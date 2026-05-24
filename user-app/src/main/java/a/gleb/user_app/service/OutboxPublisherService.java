package a.gleb.user_app.service;

import a.gleb.user_app.db.entity.OutboxEventEntity;
import a.gleb.user_app.exception.UserAppException;
import a.gleb.fellow_worker.kafka.event.UserEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisherService {

    private static final String BINDING_NAME = "user-events-out-0";

    private final StreamBridge streamBridge;
    private final JsonMapper jsonMapper;

    /**
     * Publishes an outbox event to Kafka.
     * Not transactional — Kafka send must not be rolled back with the DB transaction.
     */
    public void publish(OutboxEventEntity event) {
        var payload = jsonMapper.readValue(event.getPayload(), UserEventPayload.class);

        var message = MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.KEY, event.getAggregateId().toString().getBytes(StandardCharsets.UTF_8))
                .build();

        var sent = streamBridge.send(BINDING_NAME, message);
        if (!sent) {
            throw new UserAppException(
                    "StreamBridge failed to send event [id=%s, binding=%s]".formatted(event.getId(), BINDING_NAME)
            );
        }

        log.debug("OutboxPublisherService: event sent [id={}, eventType={}]", event.getId(), event.getEventType());
    }
}
