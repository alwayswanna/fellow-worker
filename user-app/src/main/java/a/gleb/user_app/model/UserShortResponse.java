package a.gleb.user_app.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Slim user representation for search results.")
public record UserShortResponse(

        @Schema(description = "User identifier")
        UUID id,

        @Schema(description = "Unique user login", example = "john_doe")
        String login,

        @Schema(description = "First name", example = "John")
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        String lastName
) {}
