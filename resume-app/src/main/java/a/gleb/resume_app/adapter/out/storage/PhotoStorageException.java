package a.gleb.resume_app.adapter.out.storage;

/**
 * Non-retryable MinIO failure (bad config/credentials, malformed response) — retrying it would
 * just add latency before the same failure.
 */
public class PhotoStorageException extends RuntimeException {

    public PhotoStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
