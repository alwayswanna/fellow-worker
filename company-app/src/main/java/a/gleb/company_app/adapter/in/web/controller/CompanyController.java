package a.gleb.company_app.adapter.in.web.controller;

import a.gleb.company_app.adapter.in.web.PageQueryWebMapper;
import a.gleb.company_app.adapter.in.web.advice.PageResponseMapper;
import a.gleb.company_app.adapter.in.web.controller.docs.CompanySwagger;
import a.gleb.company_app.adapter.in.web.dto.CompanyRequest;
import a.gleb.company_app.adapter.in.web.dto.CompanyResponse;
import a.gleb.company_app.adapter.in.web.mapper.CompanyWebMapper;
import a.gleb.company_app.application.service.CompanyService;
import a.gleb.company_app.domain.model.enums.CompanySize;
import a.gleb.fellow_worker.http.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController implements CompanySwagger {

    private final CompanyService companyService;
    private final CompanyWebMapper companyWebMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyResponse create(@Valid @RequestBody CompanyRequest request) {
        return companyWebMapper.toResponse(companyService.create(companyWebMapper.toDomain(request)));
    }

    @GetMapping("/my")
    public ResponseEntity<CompanyResponse> getMyCompany() {
        return companyService.findMyCompany()
                .map(company -> ResponseEntity.ok(companyWebMapper.toResponse(company)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /**
     * Internal, service-to-service lookup (no auth forwarding between services yet, so this stays
     * public like the single-company GET below) — lets callers like vacancy-app resolve display
     * data for a whole page of companies in one round trip instead of one request per distinct id.
     */
    @GetMapping("/batch")
    public List<CompanyResponse> findByIds(@RequestParam List<UUID> ids) {
        return companyService.findAllById(ids).stream().map(companyWebMapper::toResponse).toList();
    }

    @Override
    @GetMapping("/{id}")
    public CompanyResponse findById(@PathVariable UUID id) {
        return companyWebMapper.toResponse(companyService.findById(id));
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
        var filter = companyWebMapper.toFilter(name, industry, city, size);
        var page = companyService.findAll(filter, PageQueryWebMapper.from(pageable));
        return PageResponseMapper.from(page, companyWebMapper::toResponse);
    }

    @Override
    @PutMapping("/{id}")
    public CompanyResponse update(@PathVariable UUID id, @Valid @RequestBody CompanyRequest request) {
        return companyWebMapper.toResponse(companyService.update(id, companyWebMapper.toDomain(request)));
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        companyService.delete(id);
    }
}
