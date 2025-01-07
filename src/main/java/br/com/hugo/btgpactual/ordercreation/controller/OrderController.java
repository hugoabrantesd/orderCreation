package br.com.hugo.btgpactual.ordercreation.controller;

import br.com.hugo.btgpactual.ordercreation.dto.ApiResponseDto;
import br.com.hugo.btgpactual.ordercreation.dto.OrderDto;
import br.com.hugo.btgpactual.ordercreation.dto.PaginationResponseDto;
import br.com.hugo.btgpactual.ordercreation.service.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<ApiResponseDto<OrderDto>> listOrders(@PathVariable Long customerId,
                                                               @RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        var result = orderService.findAllPaging(customerId, PageRequest.of(page, pageSize));
        var totalOnOrders = orderService.findTotalOnOrdersByCustomerId(customerId);

        return ResponseEntity.ok(new ApiResponseDto<>(
                Map.of("totalOnOrders", totalOnOrders),
                result.getContent(), PaginationResponseDto.fromPage(result)));
    }

}
