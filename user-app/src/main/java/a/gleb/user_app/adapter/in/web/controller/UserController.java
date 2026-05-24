package a.gleb.user_app.adapter.in.web.controller;

import a.gleb.user_app.adapter.in.web.PageQueryWebMapper;
import a.gleb.user_app.adapter.in.web.controller.docs.UserSwagger;
import a.gleb.user_app.adapter.in.web.dto.ChangePasswordRequest;
import a.gleb.user_app.adapter.in.web.dto.UserResponse;
import a.gleb.user_app.adapter.in.web.dto.UserShortResponse;
import a.gleb.user_app.adapter.in.web.dto.UserUpdateRequest;
import a.gleb.user_app.adapter.in.web.mapper.UserWebMapper;
import a.gleb.user_app.application.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserSwagger {

    private final UserService userService;
    private final UserWebMapper userWebMapper;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse register(
            @RequestParam @NotBlank String login,
            @RequestParam @NotBlank String firstName,
            @RequestParam @NotBlank String lastName,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam @NotBlank String password,
            @RequestParam @NotBlank String code,
            @RequestParam(required = false) MultipartFile photo) {
        var command = userWebMapper.toRegisterCommand(login, firstName, lastName, birthDate, password, code);
        return userWebMapper.toResponse(userService.register(command, photo));
    }

    @GetMapping("/me")
    public UserResponse getMe(Authentication authentication) {
        return userWebMapper.toResponse(userService.getByLogin(authentication.getName()));
    }

    @PutMapping("/me")
    public UserResponse updateMe(Authentication authentication, @RequestBody @Valid UserUpdateRequest request) {
        var updated = userService.update(authentication.getName(), userWebMapper.toCommand(request));
        return userWebMapper.toResponse(updated);
    }

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(Authentication authentication, @RequestBody @Validated ChangePasswordRequest request) {
        userService.changePassword(authentication.getName(), userWebMapper.toCommand(request));
    }

    @PostMapping(value = "/me/photo", consumes = "multipart/form-data")
    public UserResponse uploadPhoto(Authentication authentication, @RequestParam("file") MultipartFile file) {
        return userWebMapper.toResponse(userService.uploadPhoto(authentication.getName(), file));
    }

    @GetMapping("/search")
    public List<UserShortResponse> search(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return userService.searchUsers(query, PageQueryWebMapper.from(pageable)).stream()
                .map(userWebMapper::toShortResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserShortResponse getById(@PathVariable UUID id) {
        return userWebMapper.toShortResponse(userService.getShortById(id));
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable UUID id) {
        var photo = userService.getPhoto(id);
        if (photo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(detectContentType(photo.objectKey())).body(photo.bytes());
    }

    private MediaType detectContentType(String objectKey) {
        if (objectKey.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (objectKey.endsWith(".gif")) return MediaType.IMAGE_GIF;
        return MediaType.IMAGE_JPEG;
    }
}
