package br.com.hugo.btgpactual.ordercreation.dto;

import br.com.hugo.btgpactual.ordercreation.factory.OrderEntityFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderDtoTest {

    @Nested
    class FromEntity {
        @Test
        void shouldMapCorrectly() {

            // Arrange
            var input = OrderEntityFactory.build();

            // Act
            var output = OrderDto.fromEntity(input);

            // Assert
            assertEquals(input.getOrderId(), output.orderId());
            assertEquals(input.getCustomerId(), output.customerId());
            assertEquals(input.getTotal(), output.total());

        }
    }

}