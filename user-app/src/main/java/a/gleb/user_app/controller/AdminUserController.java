package a.gleb.user_app.controller;

import a.gleb.user_app.controller.docs.AdminUserSwagger;
import a.gleb.user_app.model.UserResponse;
import a.gleb.user_app.model.UserUpdateRequest;
import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.user_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
public class AdminUserController implements AdminUserSwagger {

    private final UserService userService;

    @GetMapping
    public PageResponse<UserResponse> getAll(@PageableDefault(size = 20) Pageable pageable) {
        var p = userService.findAll(pageable);
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @RequestBody UserUpdateRequest request, Authentication authentication) {
        return userService.updateById(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.deleteById(id);
    }
}
