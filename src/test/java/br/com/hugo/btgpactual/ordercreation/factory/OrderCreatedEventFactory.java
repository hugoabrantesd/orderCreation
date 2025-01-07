package br.com.hugo.btgpactual.ordercreation.factory;

import br.com.hugo.btgpactual.ordercreation.dto.OrderCreatedEvent;
import br.com.hugo.btgpactual.ordercreation.dto.OrderItemEvent;

import java.math.BigDecimal;
import java.util.List;

public class OrderCreatedEventFactory {

    public static OrderCreatedEvent buildWithOneItem() {
        var items = List.of(
                OrderItemEvent.builder()
                        .produto("Monitor")
                        .quantidade(1)
                        .preco(BigDecimal.valueOf(1000))
                        .build()
        );

        return OrderCreatedEvent.builder()
                .codigoPedido(1L)
                .codigoCliente(1L)
                .itens(items)
                .build();
    }

    public static OrderCreatedEvent buildWithTwoItems() {
        var items = List.of(
                OrderItemEvent.builder()
                        .produto("Monitor")
                        .quantidade(1)
                        .preco(BigDecimal.valueOf(1000))
                        .build(),
                OrderItemEvent.builder()
                        .produto("Mouse")
                        .quantidade(1)
                        .preco(BigDecimal.valueOf(50))
                        .build()
        );

        return OrderCreatedEvent.builder()
                .codigoPedido(1L)
                .codigoCliente(1L)
                .itens(items)
                .build();
    }

}
