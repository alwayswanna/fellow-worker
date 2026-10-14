package a.gleb.resume_app.adapter.out.storage;

/**
 * Likely-transient MinIO failure (network blip, momentary server trouble) — {@code @Retryable}
 * on {@link MinioPhotoStorageAdapter} retries on this type specifically. Extends
 * {@link PhotoStorageException} so both map to the same 503 in {@code ErrorHandlerController}
 * once retries (and, once open, the circuit breaker) are exhausted.
 */
public class TransientPhotoStorageException extends PhotoStorageException {

    public TransientPhotoStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
