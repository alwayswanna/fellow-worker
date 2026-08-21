package a.gleb.user_app.adapter.in.web.controller;

import a.gleb.user_app.adapter.in.web.controller.docs.RoleSwagger;
import a.gleb.user_app.adapter.in.web.dto.RoleResponse;
import a.gleb.user_app.adapter.in.web.mapper.RoleWebMapper;
import a.gleb.user_app.application.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleController implements RoleSwagger {

    private final RoleService roleService;
    private final RoleWebMapper roleWebMapper;

    @GetMapping
    public List<RoleResponse> getAll() {
        return roleService.findAll().stream()
                .map(roleWebMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public RoleResponse getById(@PathVariable UUID id) {
        return roleWebMapper.toResponse(roleService.findById(id));
    }
}
