package a.gleb.resume_app.adapter.out.storage;

import a.gleb.resume_app.application.port.out.PhotoStoragePort;
import a.gleb.resume_app.config.properties.ResumeAppConfigurationProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioPhotoStorageAdapter implements PhotoStoragePort {

    private static final int MAX_ATTEMPTS = 3;

    private final MinioClient minioClient;
    private final ResumeAppConfigurationProperties properties;

    @Override
    @Retryable(retryFor = RuntimeException.class, maxAttempts = MAX_ATTEMPTS, backoff = @Backoff(delay = 500, multiplier = 2))
    public String upload(UUID resumeId, MultipartFile file) {
        var minio = properties.minio();
        String objectKey = "photos/" + resumeId + "." + getExtension(file.getOriginalFilename());
        try {
            ensureBucketExists();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minio.bucket())
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload photo to MinIO", e);
        }
        return minio.endpoint() + "/" + minio.bucket() + "/" + objectKey;
    }

    @Override
    @Retryable(retryFor = RuntimeException.class, maxAttempts = MAX_ATTEMPTS, backoff = @Backoff(delay = 500, multiplier = 2))
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
            throw new RuntimeException("Failed to delete photo from MinIO", e);
        }
    }

    @Recover
    public void recoverDelete(RuntimeException e, String photoUrl) {
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

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
