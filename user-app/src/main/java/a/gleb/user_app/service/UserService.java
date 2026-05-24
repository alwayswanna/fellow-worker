package a.gleb.user_app.service;

import a.gleb.user_app.db.entity.UserEntity;
import a.gleb.user_app.db.repository.RoleEntityRepository;
import a.gleb.user_app.db.repository.UserEntityRepository;
import a.gleb.user_app.exception.UserAppException;
import a.gleb.user_app.mapper.UserMapper;
import a.gleb.user_app.model.ChangePasswordRequest;
import a.gleb.user_app.model.UserRequest;
import a.gleb.user_app.model.UserResponse;
import a.gleb.user_app.model.UserShortResponse;
import a.gleb.user_app.model.UserUpdateRequest;
import a.gleb.fellow_worker.kafka.event.UserEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final OutboxEventService outboxEventService;
    private final MinioService minioService;

    @Transactional
    public UserResponse register(UserRequest request, MultipartFile photo) {
        log.info("UserService: request on register new user, [login={}]", request.login());

        if (userEntityRepository.existsUserEntityByLogin(request.login())) {
            throw new UserAppException(CONFLICT, "user with `login=%s` already exists".formatted(request.login()));
        }

        var role = roleEntityRepository.findRoleEntityByCode(request.code())
                .orElseThrow(() -> new UserAppException(NOT_FOUND, "role with `code=%s` not found".formatted(request.code())));

        var entity = userMapper.toEntity(request, role);
        entity.setPassword(passwordEncoder.encode(request.password()));
        entity.setCreatedBy(request.login());

        var saved = userEntityRepository.save(entity);

        if (photo != null && !photo.isEmpty()) {
            try {
                String photoUrl = minioService.uploadPhoto(saved.getId(), photo);
                saved.setPhotoUrl(photoUrl);
                saved = userEntityRepository.save(saved);
                log.info("UserService: photo uploaded during registration, [login={}]", saved.getLogin());
            } catch (Exception e) {
                log.warn("UserService: failed to upload photo during registration, [login={}]", request.login(), e);
            }
        }

        outboxEventService.saveEvent(saved, UserEventType.USER_CREATED);
        log.info("UserService: new user registered, [login={}]", saved.getLogin());

        return userMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getByLogin(String login) {
        var entity = findUserByUsername(login);
        if (entity == null) {
            throw new UserAppException(NOT_FOUND, "user with `login=%s` not found".formatted(login));
        }
        return userMapper.toResponse(entity);
    }

    @Transactional
    public UserResponse update(String login, UserUpdateRequest request) {
        log.info("UserService: request on update user, [login={}]", login);

        var entity = userEntityRepository.findUserEntityByLogin(login)
                .orElseThrow(() -> new UserAppException(NOT_FOUND, "user with `login=%s` not found".formatted(login)));

        userMapper.applyUpdate(entity, request, login);

        var saved = userEntityRepository.save(entity);
        outboxEventService.saveEvent(saved, UserEventType.USER_UPDATED);
        log.info("UserService: user updated, [login={}]", saved.getLogin());

        return userMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userEntityRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return userEntityRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserAppException(NOT_FOUND, "user with `id=%s` not found".formatted(id)));
    }

    @Transactional
    public UserResponse updateById(UUID id, UserUpdateRequest request, String updatedBy) {
        log.info("UserService: admin update user, [id={}]", id);

        var entity = userEntityRepository.findById(id)
                .orElseThrow(() -> new UserAppException(NOT_FOUND, "user with `id=%s` not found".formatted(id)));

        userMapper.applyUpdate(entity, request, updatedBy);

        var saved = userEntityRepository.save(entity);
        outboxEventService.saveEvent(saved, UserEventType.USER_UPDATED);
        log.info("UserService: user updated by admin, [id={}]", saved.getId());

        return userMapper.toResponse(saved);
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("UserService: admin delete user, [id={}]", id);

        var entity = userEntityRepository.findById(id)
                .orElseThrow(() -> new UserAppException(NOT_FOUND, "user with `id=%s` not found".formatted(id)));

        outboxEventService.saveEvent(entity, UserEventType.USER_DELETED);
        userEntityRepository.delete(entity);
        log.info("UserService: user deleted by admin, [id={}]", id);
    }

    public UserEntity findUserByUsername(String username) {
        return userEntityRepository.findUserEntityByLogin(username)
                .orElse(null);
    }

    @Transactional
    public void changePassword(String login, ChangePasswordRequest request) {
        log.info("UserService: change password for user [login={}]", login);

        var entity = userEntityRepository.findUserEntityByLogin(login)
                .orElseThrow(() -> new UserAppException(NOT_FOUND, "user with `login=%s` not found".formatted(login)));

        if (!passwordEncoder.matches(request.oldPassword(), entity.getPassword())) {
            throw new UserAppException(BAD_REQUEST, "Old password is incorrect");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new UserAppException(BAD_REQUEST, "New password and confirmation do not match");
        }

        entity.setPassword(passwordEncoder.encode(request.newPassword()));
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(login);
        userEntityRepository.save(entity);
        log.info("UserService: password changed for user [login={}]", login);
    }

    @Transactional
    public UserResponse uploadPhoto(String login, MultipartFile file) {
        log.info("UserService: upload photo for user [login={}]", login);

        var entity = userEntityRepository.findUserEntityByLogin(login)
                .orElseThrow(() -> new UserAppException(NOT_FOUND, "user with `login=%s` not found".formatted(login)));

        if (entity.getPhotoUrl() != null) {
            minioService.deletePhoto(entity.getPhotoUrl());
        }

        String photoUrl = minioService.uploadPhoto(entity.getId(), file);
        entity.setPhotoUrl(photoUrl);

        var saved = userEntityRepository.save(entity);
        log.info("UserService: photo uploaded for user [login={}]", login);

        return userMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getPhoto(UUID id) {
        var entity = userEntityRepository.findById(id)
                .orElseThrow(() -> new UserAppException(NOT_FOUND, "user with `id=%s` not found".formatted(id)));
        if (entity.getPhotoUrl() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] bytes = minioService.getPhotoBytes(entity.getPhotoUrl());
        MediaType contentType = detectContentType(entity.getPhotoUrl());
        return ResponseEntity.ok().contentType(contentType).body(bytes);
    }

    private MediaType detectContentType(String url) {
        if (url.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (url.endsWith(".gif")) return MediaType.IMAGE_GIF;
        return MediaType.IMAGE_JPEG;
    }

    @Transactional(readOnly = true)
    public List<UserShortResponse> searchUsers(String query, Pageable pageable) {
        return userEntityRepository.searchByQuery(query, pageable)
                .stream()
                .map(userMapper::toShortResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserShortResponse getShortById(UUID id) {
        return userEntityRepository.findById(id)
                .map(userMapper::toShortResponse)
                .orElseThrow(() -> new UserAppException(NOT_FOUND, "user with `id=%s` not found".formatted(id)));
    }

    public void save(UserEntity userEntity) {
        userEntityRepository.save(userEntity);
    }
}
