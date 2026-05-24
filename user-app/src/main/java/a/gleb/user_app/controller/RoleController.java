package a.gleb.user_app.controller;

import a.gleb.user_app.controller.docs.RoleSwagger;
import a.gleb.user_app.model.RoleResponse;
import a.gleb.user_app.service.RoleService;
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

    @GetMapping
    public List<RoleResponse> getAll() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    public RoleResponse getById(@PathVariable UUID id) {
        return roleService.findById(id);
    }
}
