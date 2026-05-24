package a.gleb.user_app.service;

import a.gleb.user_app.config.properties.UserAppConfigurationProperties;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final UserAppConfigurationProperties properties;

    public String uploadPhoto(UUID userId, MultipartFile file) {
        String objectKey = "photos/" + userId + "." + getExtension(file.getOriginalFilename());
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
            throw new RuntimeException("Failed to upload photo to MinIO", e);
        }

        return minioProperties.endpoint() + "/" + minioProperties.bucket() + "/" + objectKey;
    }

    public byte[] getPhotoBytes(String minioUrl) {
        var minioProperties = properties.minio();
        String prefix = minioProperties.endpoint() + "/" + minioProperties.bucket() + "/";
        String objectKey = minioUrl.substring(prefix.length());
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioProperties.bucket())
                .object(objectKey)
                .build())) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get photo from MinIO", e);
        }
    }

    public void deletePhoto(String photoUrl) {
        var minioProperties = properties.minio();
        String prefix = minioProperties.endpoint() + "/" + minioProperties.bucket() + "/";
        if (photoUrl == null || !photoUrl.startsWith(prefix)) {
            return;
        }
        String objectKey = photoUrl.substring(prefix.length());
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.bucket())
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to delete photo from MinIO, objectKey={}", objectKey, e);
        }
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
