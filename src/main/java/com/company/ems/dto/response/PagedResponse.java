package com.company.ems.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic paginated response wrapper for all collection GET APIs.
 *
 * @param <T> the type of items in the content list
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    /**
     * Convenience factory method to build a PagedResponse from a Spring Data {@link Page}.
     */
    public static <T> PagedResponse<T> from(Page<T> pageResult) {
        return PagedResponse.<T>builder()
                .content(pageResult.getContent())
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();
    }
}
