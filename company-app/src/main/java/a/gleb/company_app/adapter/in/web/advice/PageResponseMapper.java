package a.gleb.company_app.adapter.in.web.advice;

import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.company_app.domain.model.PageResult;

import java.util.List;
import java.util.function.Function;

/**
 * Adapts the framework-independent {@link PageResult} to the shared {@link PageResponse} envelope.
 */
public final class PageResponseMapper {

    private PageResponseMapper() {
    }

    public static <T> PageResponse<T> from(PageResult<T> page) {
        return new PageResponse<>(page.content(), page.page(), page.size(), page.totalElements(), page.totalPages());
    }

    public static <T, R> PageResponse<R> from(PageResult<T> page, Function<T, R> mapper) {
        List<R> content = page.content().stream().map(mapper).toList();
        return new PageResponse<>(content, page.page(), page.size(), page.totalElements(), page.totalPages());
    }
}
