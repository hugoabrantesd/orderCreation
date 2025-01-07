package br.com.hugo.btgpactual.ordercreation.service;

import br.com.hugo.btgpactual.ordercreation.domain.entity.OrderEntity;
import br.com.hugo.btgpactual.ordercreation.domain.entity.OrderItem;
import br.com.hugo.btgpactual.ordercreation.dto.OrderCreatedEvent;
import br.com.hugo.btgpactual.ordercreation.dto.OrderDto;
import br.com.hugo.btgpactual.ordercreation.repository.OrderRepository;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final MongoTemplate mongoTemplate;

    public OrderService(OrderRepository orderRepository, MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void createOrder(OrderCreatedEvent event) {
        var orderEntity = new OrderEntity();
        orderEntity.setOrderId(event.codigoPedido());
        orderEntity.setCustomerId(event.codigoCliente());
        orderEntity.setTotal(getTotal(event));
        orderEntity.setItems(getOrderItems(event));

        orderRepository.save(orderEntity);
    }

    private BigDecimal getTotal(OrderCreatedEvent event) {
        return event.itens().stream()
                .map(item -> item.preco().multiply(BigDecimal.valueOf(item.quantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static List<OrderItem> getOrderItems(OrderCreatedEvent event) {
        return event.itens().stream().map(item ->
                        new OrderItem(item.produto(), item.quantidade(), item.preco()))
                .toList();
    }

    public Page<OrderDto> findAllPaging(Long customerId, PageRequest pageRequest) {
        return orderRepository.findByCustomerId(customerId, pageRequest)
                .map(OrderDto::fromEntity);
    }

    public BigDecimal findTotalOnOrdersByCustomerId(Long customerId) {
        var aggregation = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group().sum("total").as("total")
        );

        var response = mongoTemplate.aggregate(aggregation, "order", Document.class);

        if (response.getUniqueMappedResult() == null) {
            return BigDecimal.ZERO;
        }
        return convertResult(response.getUniqueMappedResult());
    }

    private BigDecimal convertResult(Document result) {
        if (result.containsKey("total")) {
            Object total = result.get("total");
            if (total instanceof org.bson.types.Decimal128) {
                return ((org.bson.types.Decimal128) total).bigDecimalValue();
            } else if (total instanceof BigDecimal) {
                return (BigDecimal) total;
            }
        }
        return BigDecimal.ZERO;
    }

}
