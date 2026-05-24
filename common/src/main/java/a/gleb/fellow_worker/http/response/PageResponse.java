package a.gleb.fellow_worker.http.response;

import java.util.List;

/**
 * Stable, framework-independent paged response envelope.
 * <p>
 * Use instead of returning {@code Page<T>} directly from controllers to avoid
 * dependency on Spring Data's internal {@code PageImpl} serialization format.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
