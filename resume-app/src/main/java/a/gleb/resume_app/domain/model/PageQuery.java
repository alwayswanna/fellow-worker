package a.gleb.resume_app.domain.model;

import java.util.List;

/**
 * Framework-neutral pagination request. Adapters translate to/from Spring Data's {@code Pageable}.
 */
public record PageQuery(int page, int size, List<SortOrder> sortOrders) {

    public record SortOrder(String property, boolean descending) {
    }
}
