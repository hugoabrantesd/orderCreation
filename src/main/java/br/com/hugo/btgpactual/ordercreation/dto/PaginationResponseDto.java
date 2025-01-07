package br.com.hugo.btgpactual.ordercreation.dto;

import org.springframework.data.domain.Page;

public record PaginationResponseDto(Integer page, Integer pageSize, Integer totalElements,
                                    Integer totalPages) {

    public static PaginationResponseDto fromPage(Page<?> page) {
        return new PaginationResponseDto(
                page.getNumber(),
                page.getSize(),
                (int) page.getTotalElements(),
                page.getTotalPages()
        );

    }
}
