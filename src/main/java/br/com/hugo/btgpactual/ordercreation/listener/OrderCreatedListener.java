package br.com.hugo.btgpactual.ordercreation.listener;

import br.com.hugo.btgpactual.ordercreation.dto.OrderCreatedEvent;
import br.com.hugo.btgpactual.ordercreation.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import static br.com.hugo.btgpactual.ordercreation.config.RabbitMqConfig.CREATE_ORDER_QUEUE;

@Component
public class OrderCreatedListener {

    private OrderService orderService;

    public OrderCreatedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = CREATE_ORDER_QUEUE)
    public void listen(Message<OrderCreatedEvent> message) {
        orderService.createOrder(message.getPayload());
    }

}
