package a.gleb.resume_app.controller;

import a.gleb.resume_app.controller.docs.ResumeSwagger;
import a.gleb.resume_app.model.request.ResumeRequest;
import a.gleb.resume_app.model.response.ResumeResponse;
import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.resume_app.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController implements ResumeSwagger {

    private final ResumeService resumeService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResumeResponse create(@Valid @RequestBody ResumeRequest request) {
        return resumeService.create(request);
    }

    @Override
    @GetMapping("/{id}")
    public ResumeResponse findById(@PathVariable UUID id) {
        return resumeService.findById(id);
    }

    @Override
    @GetMapping
    public PageResponse<ResumeResponse> findAll(@PageableDefault(size = 20) Pageable pageable) {
        var p = resumeService.findAll(pageable);
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Override
    @GetMapping("/my")
    public List<ResumeResponse> findMy() {
        return resumeService.findMy();
    }

    @Override
    @PutMapping("/{id}")
    public ResumeResponse update(@PathVariable UUID id, @Valid @RequestBody ResumeRequest request) {
        return resumeService.update(id, request);
    }

    @Override
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResumeResponse uploadPhoto(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        return resumeService.uploadPhoto(id, file);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        resumeService.delete(id);
    }
}
