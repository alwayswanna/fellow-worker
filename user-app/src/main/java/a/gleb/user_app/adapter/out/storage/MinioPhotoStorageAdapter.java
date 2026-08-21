package a.gleb.user_app.adapter.out.storage;

import a.gleb.user_app.application.port.out.PhotoStoragePort;
import a.gleb.user_app.config.properties.UserAppConfigurationProperties;
import io.minio.*;
import io.minio.errors.InternalException;
import io.minio.errors.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioPhotoStorageAdapter implements PhotoStoragePort {

    private static final String PHOTO_KEY_PREFIX = "photos/";
    private static final String MAX_ATTEMPTS_EXPRESSION = "${app.minio.max-retries}";

    private final MinioClient minioClient;
    private final UserAppConfigurationProperties properties;

    /**
     * @return the object key the photo was stored under (not a full URL - the endpoint/bucket
     * are infrastructure details resolved at read time from configuration).
     */
    @Override
    @Retryable(retryFor = MinioTransientException.class, maxAttemptsExpression = MAX_ATTEMPTS_EXPRESSION, backoff = @Backoff(delay = 500, multiplier = 2))
    public String upload(UUID userId, MultipartFile file) {
        String objectKey = PHOTO_KEY_PREFIX + userId + "." + getExtension(file.getOriginalFilename());
        var minioProperties = properties.minio();
        try {
            ensureBucketExists();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.bucket())
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            throw toMinioException("Failed to upload photo to MinIO", e);
        }

        return objectKey;
    }

    @Override
    @Retryable(retryFor = MinioTransientException.class, maxAttemptsExpression = MAX_ATTEMPTS_EXPRESSION, backoff = @Backoff(delay = 500, multiplier = 2))
    public byte[] getBytes(String objectKey) {
        var minioProperties = properties.minio();
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioProperties.bucket())
                .object(objectKey)
                .build())) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw toMinioException("Failed to get photo from MinIO", e);
        }
    }

    /**
     * Best-effort deletion: never propagates a failure to the caller, since a leftover
     * orphaned object in MinIO is preferable to failing the operation that triggered it
     * (e.g. uploading a replacement photo).
     */
    @Override
    @Retryable(retryFor = MinioTransientException.class, maxAttemptsExpression = MAX_ATTEMPTS_EXPRESSION, backoff = @Backoff(delay = 500, multiplier = 2))
    public void delete(String objectKey) {
        if (objectKey == null) {
            return;
        }
        var minioProperties = properties.minio();
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.bucket())
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            var classified = toMinioException("Failed to delete photo from MinIO", e);
            if (classified instanceof MinioTransientException transientException) {
                throw transientException;
            }
            log.warn("Failed to delete photo from MinIO (non-retryable), objectKey={}", objectKey, classified);
        }
    }

    @Recover
    public void recoverDelete(MinioTransientException e, String objectKey) {
        log.warn("Failed to delete photo from MinIO after retries, objectKey={}", objectKey, e);
    }

    /**
     * Classifies MinIO SDK failures: network/IO and server-side errors are transient and
     * worth retrying; everything else (bad credentials, malformed request/response, missing
     * bucket) will not be fixed by retrying.
     */
    private RuntimeException toMinioException(String message, Exception cause) {
        return switch (cause) {
            case IOException e -> new MinioTransientException(message, e);
            case InternalException e -> new MinioTransientException(message, e);
            case ServerException e -> new MinioTransientException(message, e);
            default -> new MinioOperationException(message, cause);
        };
    }

    private void ensureBucketExists() throws Exception {
        var minioProperties = properties.minio();
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.bucket())
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.bucket())
                    .build());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
