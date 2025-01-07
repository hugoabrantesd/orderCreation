package br.com.hugo.btgpactual.ordercreation.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {

    private String product;

    private Integer quantity;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;

}
