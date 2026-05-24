package a.gleb.fellow_worker.kafka.event;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
