package a.gleb.company_app.adapter.in.web.controller;

import a.gleb.company_app.adapter.in.web.PageQueryWebMapper;
import a.gleb.company_app.adapter.in.web.advice.PageResponseMapper;
import a.gleb.company_app.adapter.in.web.controller.docs.CompanyRecruiterSwagger;
import a.gleb.company_app.adapter.in.web.dto.AddRecruiterRequest;
import a.gleb.company_app.adapter.in.web.dto.CompanyRecruiterResponse;
import a.gleb.company_app.adapter.in.web.mapper.CompanyRecruiterWebMapper;
import a.gleb.company_app.application.service.CompanyRecruiterService;
import a.gleb.fellow_worker.http.response.PageResponse;
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
@RequestMapping("/api/v1/companies/{companyId}/recruiters")
public class CompanyRecruiterController implements CompanyRecruiterSwagger {

    private final CompanyRecruiterService recruiterService;
    private final CompanyRecruiterWebMapper companyRecruiterWebMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyRecruiterResponse addRecruiter(
            @PathVariable UUID companyId,
            @Valid @RequestBody AddRecruiterRequest request
    ) {
        return companyRecruiterWebMapper.toResponse(recruiterService.addRecruiter(companyId, request.accountId()));
    }

    @Override
    @GetMapping
    public PageResponse<CompanyRecruiterResponse> findByCompany(
            @PathVariable UUID companyId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        var page = recruiterService.findByCompany(companyId, PageQueryWebMapper.from(pageable));
        return PageResponseMapper.from(page, companyRecruiterWebMapper::toResponse);
    }

    /**
     * Internal, service-to-service existence check — public like the single-company GET on
     * CompanyController, since services don't forward end-user auth to each other yet. Used by
     * vacancy-app to verify a caller is a recruiter at the vacancy's owning company.
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<Void> existsRecruiter(@PathVariable UUID companyId, @PathVariable UUID accountId) {
        return recruiterService.exists(companyId, accountId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @Override
    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRecruiter(@PathVariable UUID companyId, @PathVariable UUID accountId) {
        recruiterService.removeRecruiter(companyId, accountId);
    }

    @Override
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSelf(@PathVariable UUID companyId) {
        recruiterService.removeSelf(companyId);
    }
}
