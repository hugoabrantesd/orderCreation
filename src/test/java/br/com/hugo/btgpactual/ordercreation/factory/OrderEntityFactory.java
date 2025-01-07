package br.com.hugo.btgpactual.ordercreation.factory;

import br.com.hugo.btgpactual.ordercreation.domain.entity.OrderEntity;
import br.com.hugo.btgpactual.ordercreation.domain.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;

public class OrderEntityFactory {

    public static OrderEntity build() {
        var item = OrderItem.builder()
                .product("Monitor")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        return OrderEntity.builder()
                .orderId(1L)
                .customerId(1L)
                .items(List.of(item))
                .total(BigDecimal.TEN)
                .build();
    }

    public static Page<OrderEntity> buildWithPage() {
        return new PageImpl<>(List.of(build()));
    }

}
