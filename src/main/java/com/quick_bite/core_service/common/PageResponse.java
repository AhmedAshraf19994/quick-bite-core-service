package com.quick_bite.core_service.common;

import java.util.List;

public record PageResponse<T>(
        int page,
        int size,
        Long totalElements,
        int totalPages,
        List<T> content,
        boolean isFirst,
        boolean isLast
) {
}
