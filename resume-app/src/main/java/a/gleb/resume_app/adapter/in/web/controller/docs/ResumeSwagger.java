package a.gleb.resume_app.adapter.in.web.controller.docs;

import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.resume_app.adapter.in.web.dto.ResumeRequest;
import a.gleb.resume_app.adapter.in.web.dto.ResumeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Resumes", description = "CRUD operations for resumes")
public interface ResumeSwagger {

    @Operation(summary = "Create a new resume")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resume created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResumeResponse create(ResumeRequest request);

    @Operation(summary = "Get a resume by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resume found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Resume not found")
    })
    ResumeResponse findById(
            @Parameter(description = "Resume ID", required = true) UUID id
    );

    @Operation(summary = "List all resumes (paginated, admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of resumes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Caller is not an admin")
    })
    PageResponse<ResumeResponse> findAll(Pageable pageable);

    @Operation(summary = "List resumes belonging to the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of resumes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    List<ResumeResponse> findMy();

    @Operation(summary = "List resumes belonging to another account (recruiter/company founder only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of resumes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Caller is not a recruiter or company founder")
    })
    List<ResumeResponse> findAllByAccountId(
            @Parameter(description = "Owning account's id", required = true) UUID accountId
    );

    @Operation(
            summary = "Update a resume",
            description = "Full replace, not a merge/patch: experience/education/links must be "
                    + "provided explicitly on every call (empty array clears existing entries)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resume updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Resume not found"),
            @ApiResponse(responseCode = "409", description = "Resume was modified concurrently, please retry")
    })
    ResumeResponse update(
            @Parameter(description = "Resume ID", required = true) UUID id,
            ResumeRequest request
    );

    @Operation(summary = "Upload a photo for a resume")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo uploaded, resume returned with updated photoUrl"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Resume not found"),
            @ApiResponse(responseCode = "503", description = "Photo storage temporarily unavailable, please retry later")
    })
    ResumeResponse uploadPhoto(
            @Parameter(description = "Resume ID", required = true) UUID id,
            MultipartFile file
    );

    @Operation(summary = "Delete a resume")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resume deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Resume not found")
    })
    void delete(
            @Parameter(description = "Resume ID", required = true) UUID id
    );
}
