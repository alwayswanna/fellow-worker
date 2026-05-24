package a.gleb.company_app.controller;

import a.gleb.company_app.controller.docs.CompanyRecruiterSwagger;
import a.gleb.company_app.model.request.AddRecruiterRequest;
import a.gleb.company_app.model.response.CompanyRecruiterResponse;
import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.company_app.service.CompanyRecruiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/companies/{companyId}/recruiters")
public class CompanyRecruiterController implements CompanyRecruiterSwagger {

    private final CompanyRecruiterService recruiterService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyRecruiterResponse addRecruiter(
            @PathVariable UUID companyId,
            @Valid @RequestBody AddRecruiterRequest request
    ) {
        return recruiterService.addRecruiter(companyId, request);
    }

    @Override
    @GetMapping
    public PageResponse<CompanyRecruiterResponse> findByCompany(
            @PathVariable UUID companyId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        var p = recruiterService.findByCompany(companyId, pageable);
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
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
