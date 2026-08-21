package a.gleb.user_app.adapter.out.messaging;

import a.gleb.user_app.application.port.out.EventPublisherPort;
import a.gleb.user_app.domain.exception.EventPublishingException;
import a.gleb.user_app.domain.model.OutboxEvent;
import a.gleb.fellow_worker.kafka.event.UserEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxKafkaPublisherAdapter implements EventPublisherPort {

    private static final String BINDING_NAME = "user-events-out-0";

    private final StreamBridge streamBridge;
    private final JsonMapper jsonMapper;

    /**
     * Publishes an outbox event to Kafka.
     * Not transactional - Kafka send must not be rolled back with the DB transaction.
     */
    @Override
    public void publish(OutboxEvent event) {
        var payload = jsonMapper.readValue(event.getPayload(), UserEventPayload.class);

        var message = MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.KEY, event.getAggregateId().toString().getBytes(StandardCharsets.UTF_8))
                .build();

        var sent = streamBridge.send(BINDING_NAME, message);
        if (!sent) {
            throw new EventPublishingException(
                    "StreamBridge failed to send event [id=%s, binding=%s]".formatted(event.getId(), BINDING_NAME)
            );
        }

        log.debug("OutboxKafkaPublisherAdapter: event sent [id={}, eventType={}]", event.getId(), event.getEventType());
    }
}
