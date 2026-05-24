package a.gleb.user_app.adapter.in.web.controller;

import a.gleb.user_app.adapter.in.web.PageQueryWebMapper;
import a.gleb.user_app.adapter.in.web.advice.PageResponseMapper;
import a.gleb.user_app.adapter.in.web.controller.docs.AdminUserSwagger;
import a.gleb.user_app.adapter.in.web.dto.UserResponse;
import a.gleb.user_app.adapter.in.web.dto.UserUpdateRequest;
import a.gleb.user_app.adapter.in.web.mapper.UserWebMapper;
import a.gleb.user_app.application.service.UserService;
import a.gleb.fellow_worker.http.response.PageResponse;
import jakarta.validation.Valid;
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
    private final UserWebMapper userWebMapper;

    @GetMapping
    public PageResponse<UserResponse> getAll(@PageableDefault(size = 20) Pageable pageable) {
        var page = userService.findAll(PageQueryWebMapper.from(pageable));
        return PageResponseMapper.from(page, userWebMapper::toResponse);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userWebMapper.toResponse(userService.findById(id));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @RequestBody @Valid UserUpdateRequest request, Authentication authentication) {
        var updated = userService.updateById(id, userWebMapper.toCommand(request), authentication.getName());
        return userWebMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.deleteById(id);
    }
}
