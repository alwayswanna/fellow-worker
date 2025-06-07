package a.gleb.oauth2server.service;

import a.gleb.oauth2server.configuration.properties.OAuth2ServerConfigurationProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.MessageTimeoutException;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagePublisherService {

    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;
    private final OAuth2ServerConfigurationProperties properties;

    public void send(@NonNull String bindingName, @NonNull Object messageToSend) {
        var correlationData = new CorrelationData(UUID.randomUUID().toString());
        var message = MessageBuilder.withPayload(messageToSend)
                .setHeader(AmqpHeaders.PUBLISH_CONFIRM_CORRELATION, correlationData)
                .build();
        streamBridge.send(bindingName, message);
        awaitForConfirm(correlationData, message);
    }


    private void awaitForConfirm(@NonNull CorrelationData correlationData, @NonNull Message<?> message) {
        try {
            var confirm =
                    correlationData.getFuture().get(properties.getRmq().getAwaitTimeout(), MILLISECONDS);

            if (confirm == null) {
                log.error("Confirm is null");
                throw new AmqpException("Confirm is null");
            } else if (!confirm.isAck()) {
                log.error("Negative publisher confirm received: {}", confirm);
                throw new AmqpException("Negative publisher confirm received: " + confirm);
            } else if (correlationData.getReturned() != null) {
                log.error("Message was returned by the broker");
                throw new AmqpException("Message was returned by the broker");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted exception", e);
            Thread.currentThread().interrupt();
            throw new AmqpException(e);
        } catch (ExecutionException e) {
            log.error("Failed to get publisher confirm", e);
            throw new AmqpException("Failed to get publisher confirm", e);
        } catch (TimeoutException e) {
            log.error("Timed out awaiting publisher confirm", e);
            throw new MessageTimeoutException(message, "Timed out awaiting publisher confirm", e);
        }
    }
}
