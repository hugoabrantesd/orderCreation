package br.com.hugo.btgpactual.ordercreation.factory;

import br.com.hugo.btgpactual.ordercreation.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponseFactory {

    public static Page<OrderDto> buildWithOneElement() {
        var orderResponse = OrderDto.builder()
                .orderId(1L)
                .customerId(1L)
                .total(BigDecimal.valueOf(100))
                .build();
        return new PageImpl<>(List.of(orderResponse));
    }

}
