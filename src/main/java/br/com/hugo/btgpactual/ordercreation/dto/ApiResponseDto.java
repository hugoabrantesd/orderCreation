package br.com.hugo.btgpactual.ordercreation.dto;

import java.util.List;
import java.util.Map;

public record ApiResponseDto<T>(Map<String, Object> sumary, List<T> data, PaginationResponseDto paginationResponse) {
}
