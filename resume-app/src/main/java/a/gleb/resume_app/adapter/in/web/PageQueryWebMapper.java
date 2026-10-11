package a.gleb.resume_app.adapter.in.web;

import a.gleb.resume_app.domain.model.PageQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

/**
 * Bridges Spring Data's {@code Pageable} (resolved by Spring MVC from `page`/`size`/`sort` query
 * params) to the framework-neutral {@link PageQuery} at the web-adapter boundary.
 */
public final class PageQueryWebMapper {

    /**
     * Allow-list of {@code ResumeEntity} top-level columns that `?sort=` may reference. Without
     * this, an unknown property sails through to {@code Sort.Order} and only fails deep in the
     * repository as a {@code PropertyReferenceException}, surfacing as a generic 500 instead of a 400.
     */
    private static final Set<String> SORTABLE_PROPERTIES = Set.of(
            "id", "accountId", "firstName", "lastName", "email", "phone",
            "desiredPosition", "birthDate", "createdAt", "updatedAt"
    );

    private PageQueryWebMapper() {
    }

    public static PageQuery from(Pageable pageable) {
        var sortOrders = pageable.getSort().stream()
                .map(order -> {
                    if (!SORTABLE_PROPERTIES.contains(order.getProperty())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Cannot sort by '%s', expected one of %s".formatted(order.getProperty(), SORTABLE_PROPERTIES));
                    }
                    return new PageQuery.SortOrder(order.getProperty(), order.isDescending());
                })
                .toList();
        return new PageQuery(pageable.getPageNumber(), pageable.getPageSize(), sortOrders);
    }
}
