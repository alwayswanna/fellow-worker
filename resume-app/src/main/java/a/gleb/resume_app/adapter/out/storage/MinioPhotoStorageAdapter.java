package a.gleb.resume_app.adapter.out.storage;

import a.gleb.resume_app.application.port.out.PhotoStoragePort;
import a.gleb.resume_app.config.properties.ResumeAppConfigurationProperties;
import a.gleb.resume_app.constant.ResumeAppConstant;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;

/**
 * Only {@link java.io.IOException}/{@link io.minio.errors.InternalException}/{@link io.minio.errors.ServerException}/
 * {@link io.minio.errors.InsufficientDataException} are treated as transient and wrapped into
 * {@link TransientPhotoStorageException} for {@code @Retryable} to catch — those look like network
 * blips or momentary server trouble. Everything else (bad bucket/credentials, malformed response)
 * is wrapped into the non-retryable {@link PhotoStorageException} instead, since retrying a
 * configuration-level failure three times just adds latency before the same failure.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioPhotoStorageAdapter implements PhotoStoragePort {

    private static final int MAX_ATTEMPTS = 3;

    /**
     * Shared resilience4j instance name for both methods — one circuit breaker/bulkhead per
     * downstream (MinIO), not per operation, since both compete for the same connection pool.
     */
    private static final String MINIO_RESILIENCE_INSTANCE = "minio";

    private final MinioClient minioClient;
    private final ResumeAppConfigurationProperties properties;
    private final MeterRegistry meterRegistry;

    /**
     * Best-effort: logged, not rethrown. A listener on {@link ApplicationReadyEvent} that throws
     * fails the whole application (the app had already printed "Started" — Spring still reports
     * startup failure and shuts everything down), which is a disproportionate blast radius for a
     * MinIO hiccup at boot. If the bucket genuinely doesn't exist and this failed to create it,
     * uploads will keep failing until an operator notices the warning below and fixes MinIO — but
     * the rest of the application (everything that isn't photo upload) stays up in the meantime.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void ensureBucketExistsOnStartup() {
        try {
            ensureBucketExists();
        } catch (Exception e) {
            log.warn("Failed to ensure MinIO bucket exists on startup — will retry lazily on first upload", e);
        }
    }

    @Override
    @CircuitBreaker(name = MINIO_RESILIENCE_INSTANCE)
    @Bulkhead(name = MINIO_RESILIENCE_INSTANCE)
    @Retryable(retryFor = TransientPhotoStorageException.class, maxAttempts = MAX_ATTEMPTS, backoff = @Backoff(delay = 500, multiplier = 2))
    public String upload(UUID resumeId, MultipartFile file) {
        var minio = properties.minio();
        String objectKey = "photos/" + resumeId + "." + getExtension(file.getOriginalFilename());
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minio.bucket())
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            throw classify("Failed to upload photo to MinIO", "upload", e);
        }
        return minio.endpoint() + "/" + minio.bucket() + "/" + objectKey;
    }

    @Override
    @CircuitBreaker(name = MINIO_RESILIENCE_INSTANCE)
    @Bulkhead(name = MINIO_RESILIENCE_INSTANCE)
    @Retryable(retryFor = TransientPhotoStorageException.class, maxAttempts = MAX_ATTEMPTS, backoff = @Backoff(delay = 500, multiplier = 2))
    public void delete(String photoUrl) {
        var minio = properties.minio();
        String prefix = minio.endpoint() + "/" + minio.bucket() + "/";
        if (photoUrl == null || !photoUrl.startsWith(prefix)) {
            return;
        }
        String objectKey = photoUrl.substring(prefix.length());
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minio.bucket())
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            throw classify("Failed to delete photo from MinIO", "delete", e);
        }
    }

    @Recover
    public void recoverDelete(TransientPhotoStorageException e, String photoUrl) {
        log.warn("Failed to delete photo from MinIO after retries, photoUrl={}", photoUrl, e);
    }

    private void ensureBucketExists() throws Exception {
        var minio = properties.minio();
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minio.bucket())
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minio.bucket())
                    .build());
        }
    }

    /**
     * {@link ResumeAppConstant#ALLOWED_PHOTO_EXTENSIONS} is already enforced in {@code ResumeService}
     * before this is reached — this is defense in depth so the MinIO object key is never built
     * from an unvalidated, attacker-controlled filename.
     */
    private String getExtension(String filename) {
        String extension = StringUtils.substringAfterLast(StringUtils.defaultString(filename), ".")
                .toLowerCase(Locale.ROOT);
        if (!ResumeAppConstant.ALLOWED_PHOTO_EXTENSIONS.contains(extension)) {
            throw new PhotoStorageException("Rejected unexpected photo extension: " + extension, null);
        }
        return extension;
    }

    private RuntimeException classify(String message, String operation, Exception e) {
        meterRegistry.counter("resume.photo.storage.failed", "operation", operation).increment();
        if (e instanceof ErrorResponseException || e instanceof InvalidResponseException
                || e instanceof XmlParserException || e instanceof InvalidKeyException
                || e instanceof NoSuchAlgorithmException) {
            return new PhotoStorageException(message, e);
        }
        return new TransientPhotoStorageException(message, e);
    }
}
