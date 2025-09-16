package a.gleb.apicommon.api.main_service.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update active status")
public record StatusUpdateRequest(
        @NotNull
        @Schema(description = "New active status", example = "true", required = true)
        Boolean isActive
) {}