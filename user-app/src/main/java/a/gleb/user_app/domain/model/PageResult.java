package a.gleb.user_app.domain.model;

import java.util.List;

/**
 * Framework-neutral pagination result. Mirrors the shape of {@code Page} without the Spring Data dependency.
 */
public record PageResult<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
}
