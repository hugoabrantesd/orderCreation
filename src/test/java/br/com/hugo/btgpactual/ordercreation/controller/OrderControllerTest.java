package br.com.hugo.btgpactual.ordercreation.controller;

import br.com.hugo.btgpactual.ordercreation.dto.OrderDto;
import br.com.hugo.btgpactual.ordercreation.factory.OrderResponseFactory;
import br.com.hugo.btgpactual.ordercreation.service.OrderService;
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
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Captor
    ArgumentCaptor<Long> customerIdCaptor;

    @Captor
    ArgumentCaptor<PageRequest> pageRequestCaptor;

    @Nested
    class ListOrdersTest {
        @Test
        void shouldReturnHttpOk() {
            // ARRANGE
            Long customerId = 1L;
            Integer page = 0;
            Integer pageSize = 10;
            doReturn(OrderResponseFactory.buildWithOneElement())
                    .when(orderService).findAllPaging(anyLong(), any());
            doReturn(BigDecimal.valueOf(100))
                    .when(orderService).findTotalOnOrdersByCustomerId(anyLong());

            // ACT
            var response = orderController.listOrders(customerId, page, pageSize);

            // ASSERT
            assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        }

        @Test
        void shouldReturnCorrectParametersToService() {
            // ARRANGE
            Long customerId = 1L;
            Integer page = 0;
            Integer pageSize = 10;
            doReturn(OrderResponseFactory.buildWithOneElement())
                    .when(orderService).findAllPaging(customerIdCaptor.capture(), pageRequestCaptor.capture());
            doReturn(BigDecimal.valueOf(100))
                    .when(orderService).findTotalOnOrdersByCustomerId(customerIdCaptor.capture());

            // ACT
            var response = orderController.listOrders(customerId, page, pageSize);

            // ASSERT
            assertEquals(2, customerIdCaptor.getAllValues().size());
            assertEquals(customerId, customerIdCaptor.getAllValues().get(0));
            assertEquals(customerId, customerIdCaptor.getAllValues().get(1));

            assertEquals(page, pageRequestCaptor.getValue().getPageNumber());
            assertEquals(pageSize, pageRequestCaptor.getValue().getPageSize());
        }

        @Test
        void shouldReturnCorrectBody() {
            // ARRANGE
            Long customerId = 1L;
            Integer page = 0;
            Integer pageSize = 10;
            BigDecimal totalOnOrders = BigDecimal.valueOf(100);
            Page<OrderDto> pagination = OrderResponseFactory.buildWithOneElement();

            doReturn(pagination)
                    .when(orderService).findAllPaging(anyLong(), any());
            doReturn(totalOnOrders)
                    .when(orderService).findTotalOnOrdersByCustomerId(anyLong());

            // ACT
            var response = orderController.listOrders(customerId, page, pageSize);

            // ASSERT
            assertNotNull(response);
            var body = response.getBody();

            assertNotNull(body);
            assertNotNull(body.data());
            assertNotNull(body.paginationResponse());
            assertNotNull(body.sumary());

            assertEquals(totalOnOrders, body.sumary().get("totalOnOrders"));
            assertEquals(pagination.getTotalElements(), body.paginationResponse().totalElements().intValue());
            assertEquals(pagination.getTotalPages(), body.paginationResponse().totalPages().intValue());
            assertEquals(pagination.getNumber(), body.paginationResponse().page().intValue());
            assertEquals(pagination.getSize(), body.paginationResponse().pageSize().intValue());

            assertEquals(pagination.getContent(), body.data());
        }
    }

}