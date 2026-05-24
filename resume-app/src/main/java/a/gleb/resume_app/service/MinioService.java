package a.gleb.resume_app.service;

import a.gleb.resume_app.config.properties.ResumeAppConfigurationProperties;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final ResumeAppConfigurationProperties properties;

    public String uploadPhoto(UUID resumeId, MultipartFile file) {
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

    public void deletePhoto(String photoUrl) {
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
            log.warn("Failed to delete photo from MinIO, objectKey={}", objectKey, e);
        }
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
