package a.gleb.resume_app.adapter.out.storage;

/**
 * Likely-transient MinIO failure (network blip, momentary server trouble) — {@code @Retryable}
 * on {@link MinioPhotoStorageAdapter} retries on this type specifically.
 */
public class TransientPhotoStorageException extends RuntimeException {

    public TransientPhotoStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
