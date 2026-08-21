package a.gleb.user_app.adapter.in.web.controller;

import a.gleb.user_app.adapter.in.web.PageQueryWebMapper;
import a.gleb.user_app.adapter.in.web.advice.PageResponseMapper;
import a.gleb.user_app.adapter.in.web.controller.docs.AdminRoleSwagger;
import a.gleb.user_app.adapter.in.web.dto.RoleRequest;
import a.gleb.user_app.adapter.in.web.dto.RoleResponse;
import a.gleb.user_app.adapter.in.web.dto.RoleUpdateRequest;
import a.gleb.user_app.adapter.in.web.mapper.RoleWebMapper;
import a.gleb.user_app.application.service.RoleService;
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
@RequestMapping("/api/v1/admin/roles")
public class AdminRoleController implements AdminRoleSwagger {

    private final RoleService roleService;
    private final RoleWebMapper roleWebMapper;

    @GetMapping
    public PageResponse<RoleResponse> getAll(@PageableDefault(size = 20) Pageable pageable) {
        var page = roleService.findAll(PageQueryWebMapper.from(pageable));
        return PageResponseMapper.from(page, roleWebMapper::toResponse);
    }

    @GetMapping("/{id}")
    public RoleResponse getById(@PathVariable UUID id) {
        return roleWebMapper.toResponse(roleService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse create(@RequestBody @Valid RoleRequest request, Authentication authentication) {
        var created = roleService.create(roleWebMapper.toCommand(request), authentication.getName());
        return roleWebMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public RoleResponse update(@PathVariable UUID id, @RequestBody @Valid RoleUpdateRequest request, Authentication authentication) {
        var updated = roleService.update(id, roleWebMapper.toCommand(request), authentication.getName());
        return roleWebMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        roleService.delete(id);
    }
}
