package a.gleb.user_app.adapter.out.storage;

/**
 * Wraps a non-transient MinIO failure (bad credentials, missing bucket, malformed request/response)
 * that retrying will not fix.
 */
public class MinioOperationException extends RuntimeException {

    public MinioOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
