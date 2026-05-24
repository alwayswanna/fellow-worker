package a.gleb.user_app.controller;

import a.gleb.user_app.controller.docs.AdminRoleSwagger;
import a.gleb.user_app.model.RoleRequest;
import a.gleb.user_app.model.RoleResponse;
import a.gleb.user_app.model.RoleUpdateRequest;
import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.user_app.service.RoleService;
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
@RequestMapping("/api/v1/admin/roles")
public class AdminRoleController implements AdminRoleSwagger {

    private final RoleService roleService;

    @GetMapping
    public PageResponse<RoleResponse> getAll(@PageableDefault(size = 20) Pageable pageable) {
        var p = roleService.findAll(pageable);
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @GetMapping("/{id}")
    public RoleResponse getById(@PathVariable UUID id) {
        return roleService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse create(@RequestBody @Valid RoleRequest request, Authentication authentication) {
        return roleService.create(request, authentication.getName());
    }

    @PutMapping("/{id}")
    public RoleResponse update(@PathVariable UUID id, @RequestBody RoleUpdateRequest request, Authentication authentication) {
        return roleService.update(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        roleService.delete(id);
    }
}
