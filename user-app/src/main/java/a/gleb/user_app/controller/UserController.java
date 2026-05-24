package a.gleb.user_app.controller;

import a.gleb.user_app.controller.docs.UserSwagger;
import a.gleb.user_app.model.ChangePasswordRequest;
import a.gleb.user_app.model.UserRequest;
import a.gleb.user_app.model.UserResponse;
import a.gleb.user_app.model.UserShortResponse;
import a.gleb.user_app.model.UserUpdateRequest;
import a.gleb.user_app.service.UserService;
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

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse register(
            @RequestParam @NotBlank String login,
            @RequestParam @NotBlank String firstName,
            @RequestParam @NotBlank String lastName,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam @NotBlank String password,
            @RequestParam @NotBlank String code,
            @RequestParam(required = false) MultipartFile photo) {
        return userService.register(new UserRequest(login, firstName, lastName, birthDate, password, code), photo);
    }

    @GetMapping("/me")
    public UserResponse getMe(Authentication authentication) {
        return userService.getByLogin(authentication.getName());
    }

    @PutMapping("/me")
    public UserResponse updateMe(Authentication authentication, @RequestBody UserUpdateRequest request) {
        return userService.update(authentication.getName(), request);
    }

    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(Authentication authentication, @RequestBody @Validated ChangePasswordRequest request) {
        userService.changePassword(authentication.getName(), request);
    }

    @PostMapping(value = "/me/photo", consumes = "multipart/form-data")
    public UserResponse uploadPhoto(Authentication authentication, @RequestParam("file") MultipartFile file) {
        return userService.uploadPhoto(authentication.getName(), file);
    }

    @GetMapping("/search")
    public List<UserShortResponse> search(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return userService.searchUsers(query, pageable);
    }

    @GetMapping("/{id}")
    public UserShortResponse getById(@PathVariable UUID id) {
        return userService.getShortById(id);
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable UUID id) {
        return userService.getPhoto(id);
    }
}
