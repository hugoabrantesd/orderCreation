package br.com.hugo.btgpactual.ordercreation.service;

import br.com.hugo.btgpactual.ordercreation.domain.entity.OrderEntity;
import br.com.hugo.btgpactual.ordercreation.factory.OrderCreatedEventFactory;
import br.com.hugo.btgpactual.ordercreation.factory.OrderEntityFactory;
import br.com.hugo.btgpactual.ordercreation.repository.OrderRepository;
import org.bson.Document;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    MongoTemplate mongoTemplate;

    @InjectMocks
    OrderService orderService;

    @Captor
    ArgumentCaptor<OrderEntity> orderEntityCaptor;

    @Captor
    ArgumentCaptor<Aggregation> aggregationCaptor;

    @Nested
    class Save {

        @Test
        void shouldCallRepositorySave() {
            // ARRANGE
            var event = OrderCreatedEventFactory.buildWithOneItem();

            // ACT
            orderService.createOrder(event);

            // ASSERT
            verify(orderRepository, times(1)).save(any());
        }

        @Test
        void shouldMapEventToEntityWithSuccess() {
            // ARRANGE
            var event = OrderCreatedEventFactory.buildWithOneItem();

            // ACT
            orderService.createOrder(event);

            // ASSERT
            verify(orderRepository, times(1)).save(orderEntityCaptor.capture());

            var orderEntity = orderEntityCaptor.getValue();
            assertEquals(event.codigoPedido(), orderEntity.getOrderId());
            assertEquals(event.codigoCliente(), orderEntity.getCustomerId());
            assertNotNull(orderEntity.getTotal());
            assertNotNull(orderEntity.getItems());

            assertEquals(event.itens().get(0).produto(), orderEntity.getItems().get(0).getProduct());
            assertEquals(event.itens().get(0).quantidade(), orderEntity.getItems().get(0).getQuantity());
            assertEquals(event.itens().get(0).preco(), orderEntity.getItems().get(0).getPrice());

        }

        @Test
        void shouldCalculateprderTotalWithSuccess() {
            // ARRANGE
            var event = OrderCreatedEventFactory.buildWithTwoItems();
            var totalItem1 = event.itens().get(0).preco().multiply(BigDecimal.valueOf(event.itens().get(0).quantidade()));
            var totalItem2 = event.itens().get(1).preco().multiply(BigDecimal.valueOf(event.itens().get(1).quantidade()));
            var total = totalItem1.add(totalItem2);

            // ACT
            orderService.createOrder(event);

            // ASSERT
            verify(orderRepository, times(1)).save(orderEntityCaptor.capture());

            var orderEntity = orderEntityCaptor.getValue();

            assertNotNull(orderEntity.getTotal());
            assertEquals(total.doubleValue(), orderEntity.getTotal().doubleValue());
        }
    }

    @Nested
    class FindAllPaging {

        @Test
        void shouldCallRepository() {
            // ARRANGE
            var customerId = 1L;
            var pageRequest = PageRequest.of(1, 10);

            doReturn(OrderEntityFactory.buildWithPage())
                    .when(orderRepository).findByCustomerId(eq(customerId), eq(pageRequest));

            // ACT
            var response = orderService.findAllPaging(customerId, pageRequest);

            // ASSERT
            verify(orderRepository, times(1))
                    .findByCustomerId(eq(customerId), eq(pageRequest));

        }

        @Test
        void shouldMapResponse() {
            // ARRANGE
            var customerId = 1L;
            var pageRequest = PageRequest.of(1, 10);
            Page<OrderEntity> page = OrderEntityFactory.buildWithPage();

            doReturn(page).when(orderRepository).findByCustomerId(anyLong(), any());

            // ACT
            var response = orderService.findAllPaging(customerId, pageRequest);

            // ASSERT
            assertEquals(page.getTotalElements(), response.getTotalElements());
            assertEquals(page.getTotalPages(), response.getTotalPages());
            assertEquals(page.getContent().size(), response.getContent().size());
            assertEquals(page.getNumber(), response.getNumber());

            assertEquals(page.getContent().get(0).getOrderId(), response.getContent().get(0).orderId());
            assertEquals(page.getContent().get(0).getCustomerId(), response.getContent().get(0).customerId());
            assertEquals(page.getContent().get(0).getTotal(), response.getContent().get(0).total());
        }
    }

    @Nested
    class FindTotalOnOrdersByCustomerId {

        @Test
        void shouldCallMongoTemplate() {
            // ARRANGE
            var customerId = 1L;
            var aggregationResult = mock(AggregationResults.class);
            BigDecimal totalExpected = BigDecimal.valueOf(1);

            doReturn(new Document("total", totalExpected)).when(aggregationResult).getUniqueMappedResult();
            doReturn(aggregationResult).when(mongoTemplate)
                    .aggregate(any(Aggregation.class), anyString(), eq(Document.class));

            // ACT
            var total = orderService.findTotalOnOrdersByCustomerId(customerId);

            // ASSERT
            verify(mongoTemplate, times(1))
                    .aggregate(any(Aggregation.class), anyString(), eq(Document.class));
            assertEquals(totalExpected, total);
        }

        @Test
        void shouldUseCorrectAggregation() {
            // ARRANGE
            var customerId = 1L;
            var aggregationResult = mock(AggregationResults.class);
            BigDecimal totalExpected = BigDecimal.valueOf(1);

            doReturn(new Document("total", totalExpected)).when(aggregationResult).getUniqueMappedResult();
            doReturn(aggregationResult).when(mongoTemplate)
                    .aggregate(aggregationCaptor.capture(), anyString(), eq(Document.class));

            // ACT
            orderService.findTotalOnOrdersByCustomerId(customerId);

            // ASSERT
            var aggregation = aggregationCaptor.getValue();
            var aggregationExpected = newAggregation(
                    match(Criteria.where("customerId").is(customerId)),
                    group().sum("total").as("total"));

            assertNotNull(aggregation);
            assertEquals(aggregationExpected.toString(), aggregation.toString());
        }

        @Test
        void shouldQueryCorrectTable() {
            // ARRANGE
            var customerId = 1L;
            var aggregationResult = mock(AggregationResults.class);
            BigDecimal totalExpected = BigDecimal.valueOf(1);

            doReturn(new Document("total", totalExpected)).when(aggregationResult).getUniqueMappedResult();
            doReturn(aggregationResult).when(mongoTemplate)
                    .aggregate(any(Aggregation.class), anyString(), eq(Document.class));

            // ACT
            orderService.findTotalOnOrdersByCustomerId(customerId);

            // ASSERT
            verify(mongoTemplate, times(1))
                    .aggregate(any(Aggregation.class), eq("order"), eq(Document.class));
        }
    }
}