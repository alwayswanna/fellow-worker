package a.gleb.user_app.adapter.out.storage;

/**
 * Wraps a transient MinIO failure (network/IO, server-side hiccup) that is worth retrying.
 */
public class MinioTransientException extends RuntimeException {

    public MinioTransientException(String message, Throwable cause) {
        super(message, cause);
    }
}
