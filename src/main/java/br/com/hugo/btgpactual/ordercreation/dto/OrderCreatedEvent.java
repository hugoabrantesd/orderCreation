package br.com.hugo.btgpactual.ordercreation.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderCreatedEvent(Long codigoPedido, Long codigoCliente, List<OrderItemEvent> itens) {
}
