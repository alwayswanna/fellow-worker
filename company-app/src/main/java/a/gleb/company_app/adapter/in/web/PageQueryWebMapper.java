package a.gleb.company_app.adapter.in.web;

import a.gleb.company_app.domain.model.PageQuery;
import org.springframework.data.domain.Pageable;

/**
 * Bridges Spring Data's {@code Pageable} (resolved by Spring MVC from `page`/`size`/`sort` query
 * params) to the framework-neutral {@link PageQuery} at the web-adapter boundary.
 */
public final class PageQueryWebMapper {

    private PageQueryWebMapper() {
    }

    public static PageQuery from(Pageable pageable) {
        var sortOrders = pageable.getSort().stream()
                .map(order -> new PageQuery.SortOrder(order.getProperty(), order.isDescending()))
                .toList();
        return new PageQuery(pageable.getPageNumber(), pageable.getPageSize(), sortOrders);
    }
}
