package a.gleb.resume_app.application.port.out;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Unlike a key-based photo store, {@link #upload} here returns the full public URL - resume-app
 * exposes it directly as {@code Resume.photoUrl} rather than serving bytes through a dedicated
 * endpoint, so the adapter must keep resolving endpoint/bucket into the returned value.
 */
public interface PhotoStoragePort {

    String upload(UUID resumeId, MultipartFile file);

    void delete(String photoUrl);
}
