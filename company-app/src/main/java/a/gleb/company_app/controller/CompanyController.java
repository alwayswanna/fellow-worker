package a.gleb.company_app.controller;

import a.gleb.company_app.controller.docs.CompanySwagger;
import a.gleb.company_app.model.enums.CompanySize;
import a.gleb.company_app.model.request.CompanyRequest;
import a.gleb.company_app.model.response.CompanyResponse;
import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.company_app.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController implements CompanySwagger {

    private final CompanyService companyService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponse create(@Valid @RequestBody CompanyRequest request) {
        return companyService.create(request);
    }

    @GetMapping("/my")
    public ResponseEntity<CompanyResponse> getMyCompany() {
        return companyService.findMyCompany()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Override
    @GetMapping("/{id}")
    public CompanyResponse findById(@PathVariable UUID id) {
        return companyService.findById(id);
    }

    @Override
    @GetMapping
    public PageResponse<CompanyResponse> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) CompanySize size,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        var p = companyService.findAll(name, industry, city, size, pageable);
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Override
    @PutMapping("/{id}")
    public CompanyResponse update(@PathVariable UUID id, @Valid @RequestBody CompanyRequest request) {
        return companyService.update(id, request);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        companyService.delete(id);
    }
}
