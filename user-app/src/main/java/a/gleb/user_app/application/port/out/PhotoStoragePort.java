package a.gleb.user_app.application.port.out;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * {@link MultipartFile} is kept as the transport type here (rather than converting to a raw
 * byte stream at the web boundary) so that IOException classification into transient/non-transient
 * MinIO failures keeps happening in one place, inside the adapter - matching the pre-refactor behavior.
 */
public interface PhotoStoragePort {

    String upload(UUID userId, MultipartFile file);

    byte[] getBytes(String objectKey);

    void delete(String objectKey);
}
