package a.gleb.resume_app.adapter.in.web.controller;

import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.resume_app.adapter.in.web.PageQueryWebMapper;
import a.gleb.resume_app.adapter.in.web.advice.PageResponseMapper;
import a.gleb.resume_app.adapter.in.web.controller.docs.ResumeSwagger;
import a.gleb.resume_app.adapter.in.web.dto.ResumeRequest;
import a.gleb.resume_app.adapter.in.web.dto.ResumeResponse;
import a.gleb.resume_app.adapter.in.web.mapper.ResumeWebMapper;
import a.gleb.resume_app.application.service.ResumeService;
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
    private final ResumeWebMapper resumeWebMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResumeResponse create(@Valid @RequestBody ResumeRequest request) {
        return resumeWebMapper.toResponse(resumeService.create(resumeWebMapper.toDomain(request)));
    }

    @Override
    @GetMapping("/{id}")
    public ResumeResponse findById(@PathVariable UUID id) {
        return resumeWebMapper.toResponse(resumeService.findById(id));
    }

    @Override
    @GetMapping
    public PageResponse<ResumeResponse> findAll(@PageableDefault(size = 20) Pageable pageable) {
        var page = resumeService.findAll(PageQueryWebMapper.from(pageable));
        return PageResponseMapper.from(page, resumeWebMapper::toResponse);
    }

    @Override
    @GetMapping("/my")
    public List<ResumeResponse> findMy() {
        return resumeService.findMy().stream().map(resumeWebMapper::toResponse).toList();
    }

    @Override
    @PutMapping("/{id}")
    public ResumeResponse update(@PathVariable UUID id, @Valid @RequestBody ResumeRequest request) {
        return resumeWebMapper.toResponse(resumeService.update(id, resumeWebMapper.toDomain(request)));
    }

    @Override
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResumeResponse uploadPhoto(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        return resumeWebMapper.toResponse(resumeService.uploadPhoto(id, file));
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        resumeService.delete(id);
    }
}
