package com.example.spotifyscrobble.shared;

import org.springframework.data.domain.Page;

import java.util.List;

public record CustomPageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        Long totalElements,
        int totalPages
) {
    public CustomPageResponse(Page<T> page){
        this(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
