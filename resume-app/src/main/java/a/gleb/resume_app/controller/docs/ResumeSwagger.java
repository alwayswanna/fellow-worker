package a.gleb.resume_app.controller.docs;

import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.resume_app.model.request.ResumeRequest;
import a.gleb.resume_app.model.response.ResumeResponse;
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

    @Operation(summary = "List all resumes (paginated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of resumes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    PageResponse<ResumeResponse> findAll(Pageable pageable);

    @Operation(summary = "List resumes belonging to the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of resumes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    List<ResumeResponse> findMy();

    @Operation(summary = "Update a resume")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resume updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Resume not found")
    })
    ResumeResponse update(
            @Parameter(description = "Resume ID", required = true) UUID id,
            ResumeRequest request
    );

    @Operation(summary = "Upload a photo for a resume")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo uploaded, resume returned with updated photoUrl"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Resume not found")
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
