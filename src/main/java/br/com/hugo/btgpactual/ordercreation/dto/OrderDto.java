package br.com.hugo.btgpactual.ordercreation.dto;

import br.com.hugo.btgpactual.ordercreation.domain.entity.OrderEntity;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderDto(Long orderId, Long customerId, BigDecimal total) {

    public static OrderDto fromEntity(OrderEntity orderEntity) {
        return new OrderDto(orderEntity.getOrderId(), orderEntity.getCustomerId(), orderEntity.getTotal());
    }

}
