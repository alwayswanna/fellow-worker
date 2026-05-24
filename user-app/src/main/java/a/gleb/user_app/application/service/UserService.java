package a.gleb.user_app.application.service;

import a.gleb.user_app.application.port.out.PasswordHasherPort;
import a.gleb.user_app.application.port.out.PhotoStoragePort;
import a.gleb.user_app.application.port.out.RoleRepositoryPort;
import a.gleb.user_app.application.port.out.UserRepositoryPort;
import a.gleb.user_app.config.properties.UserAppConfigurationProperties;
import a.gleb.user_app.domain.exception.InvalidOldPasswordException;
import a.gleb.user_app.domain.exception.InvalidPhotoException;
import a.gleb.user_app.domain.exception.LoginAlreadyExistsException;
import a.gleb.user_app.domain.exception.PasswordConfirmationMismatchException;
import a.gleb.user_app.domain.exception.RoleNotFoundException;
import a.gleb.user_app.domain.exception.UserNotFoundException;
import a.gleb.user_app.domain.model.PageQuery;
import a.gleb.user_app.domain.model.PageResult;
import a.gleb.user_app.domain.model.User;
import a.gleb.fellow_worker.kafka.event.UserEventType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordHasherPort passwordHasherPort;
    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final OutboxEventService outboxEventService;
    private final PhotoStoragePort photoStoragePort;
    private final MeterRegistry meterRegistry;
    private final UserAppConfigurationProperties properties;

    @Transactional
    public User register(RegisterUserCommand command, MultipartFile photo) {
        log.info("UserService: request on register new user, [login={}]", command.login());

        var hasPhoto = photo != null && !photo.isEmpty();
        if (hasPhoto) {
            validatePhoto(photo);
        }

        if (userRepositoryPort.existsByLogin(command.login())) {
            throw new LoginAlreadyExistsException("user with `login=%s` already exists".formatted(command.login()));
        }

        var role = roleRepositoryPort.findByCode(command.roleCode())
                .orElseThrow(() -> new RoleNotFoundException("role with `code=%s` not found".formatted(command.roleCode())));

        var user = User.builder()
                .login(command.login())
                .firstName(command.firstName())
                .lastName(command.lastName())
                .birthDate(command.birthDate())
                .password(passwordHasherPort.encode(command.password()))
                .role(role)
                .createdAt(LocalDateTime.now())
                .createdBy(command.login())
                .build();

        var saved = userRepositoryPort.save(user);

        if (hasPhoto) {
            try {
                String photoKey = photoStoragePort.upload(saved.getId(), photo);
                saved.setPhotoKey(photoKey);
                saved = userRepositoryPort.save(saved);
                log.info("UserService: photo uploaded during registration, [login={}]", saved.getLogin());
            } catch (Exception e) {
                log.warn("UserService: failed to upload photo during registration, [login={}]", command.login(), e);
                Counter.builder("user.photo_upload.failed")
                        .tag("phase", "registration")
                        .register(meterRegistry)
                        .increment();
            }
        }

        outboxEventService.saveEvent(saved, UserEventType.USER_CREATED);
        log.info("UserService: new user registered, [login={}]", saved.getLogin());

        return saved;
    }

    @Transactional(readOnly = true)
    public User getByLogin(String login) {
        var user = findUserByUsername(login);
        if (user == null) {
            throw new UserNotFoundException("user with `login=%s` not found".formatted(login));
        }
        return user;
    }

    @Transactional
    public User update(String login, UpdateUserCommand command) {
        log.info("UserService: request on update user, [login={}]", login);

        var user = userRepositoryPort.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("user with `login=%s` not found".formatted(login)));

        applyUpdate(user, command, login);

        var saved = userRepositoryPort.save(user);
        outboxEventService.saveEvent(saved, UserEventType.USER_UPDATED);
        log.info("UserService: user updated, [login={}]", saved.getLogin());

        return saved;
    }

    @Transactional(readOnly = true)
    public PageResult<User> findAll(PageQuery pageQuery) {
        return userRepositoryPort.findAll(pageQuery);
    }

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user with `id=%s` not found".formatted(id)));
    }

    @Transactional
    public User updateById(UUID id, UpdateUserCommand command, String updatedBy) {
        log.info("UserService: admin update user, [id={}]", id);

        var user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user with `id=%s` not found".formatted(id)));

        applyUpdate(user, command, updatedBy);

        var saved = userRepositoryPort.save(user);
        outboxEventService.saveEvent(saved, UserEventType.USER_UPDATED);
        log.info("UserService: user updated by admin, [id={}]", saved.getId());

        return saved;
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("UserService: admin delete user, [id={}]", id);

        var user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user with `id=%s` not found".formatted(id)));

        outboxEventService.saveEvent(user, UserEventType.USER_DELETED);
        userRepositoryPort.deleteById(id);
        log.info("UserService: user deleted by admin, [id={}]", id);
    }

    public User findUserByUsername(String username) {
        return userRepositoryPort.findByLogin(username).orElse(null);
    }

    @Transactional
    public void changePassword(String login, ChangePasswordCommand command) {
        log.info("UserService: change password for user [login={}]", login);

        var user = userRepositoryPort.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("user with `login=%s` not found".formatted(login)));

        if (!passwordHasherPort.matches(command.oldPassword(), user.getPassword())) {
            throw new InvalidOldPasswordException("Old password is incorrect");
        }

        if (!command.newPassword().equals(command.confirmPassword())) {
            throw new PasswordConfirmationMismatchException("New password and confirmation do not match");
        }

        user.setPassword(passwordHasherPort.encode(command.newPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(login);
        userRepositoryPort.save(user);
        log.info("UserService: password changed for user [login={}]", login);
    }

    @Transactional
    public User uploadPhoto(String login, MultipartFile file) {
        log.info("UserService: upload photo for user [login={}]", login);

        validatePhoto(file);

        var user = userRepositoryPort.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("user with `login=%s` not found".formatted(login)));

        if (user.getPhotoKey() != null) {
            photoStoragePort.delete(user.getPhotoKey());
        }

        String photoKey = photoStoragePort.upload(user.getId(), file);
        user.setPhotoKey(photoKey);

        var saved = userRepositoryPort.save(user);
        log.info("UserService: photo uploaded for user [login={}]", login);

        return saved;
    }

    @Transactional(readOnly = true)
    public PhotoContent getPhoto(UUID id) {
        var user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user with `id=%s` not found".formatted(id)));
        if (user.getPhotoKey() == null) {
            return null;
        }
        byte[] bytes = photoStoragePort.getBytes(user.getPhotoKey());
        return new PhotoContent(bytes, user.getPhotoKey());
    }

    /**
     * Rejects obviously wrong client input (size/declared content-type) before it ever reaches
     * MinIO. Relies on the client-supplied content-type header, not the actual file bytes.
     */
    private void validatePhoto(MultipartFile photo) {
        var photoUploadProperties = properties.photoUpload();

        if (photo.getSize() > photoUploadProperties.maxSizeBytes()) {
            throw new InvalidPhotoException(
                    "Photo exceeds maximum allowed size of %d bytes".formatted(photoUploadProperties.maxSizeBytes()));
        }

        var contentType = photo.getContentType();
        if (contentType == null || !photoUploadProperties.allowedContentTypes().contains(contentType)) {
            throw new InvalidPhotoException("Unsupported photo content type: %s".formatted(contentType));
        }
    }

    private void applyUpdate(User user, UpdateUserCommand command, String updatedBy) {
        if (command.firstName() != null) user.setFirstName(command.firstName());
        if (command.lastName() != null) user.setLastName(command.lastName());
        if (command.birthDate() != null) user.setBirthDate(command.birthDate());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(updatedBy);
    }

    @Transactional(readOnly = true)
    public List<User> searchUsers(String query, PageQuery pageQuery) {
        return userRepositoryPort.search(query, pageQuery);
    }

    @Transactional(readOnly = true)
    public User getShortById(UUID id) {
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user with `id=%s` not found".formatted(id)));
    }

    public void save(User user) {
        userRepositoryPort.save(user);
    }

    public record PhotoContent(byte[] bytes, String objectKey) {
    }
}
