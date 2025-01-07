package br.com.hugo.btgpactual.ordercreation.dto;

import java.math.BigDecimal;

public record OrderItemDto(String produto, Integer quantidade, BigDecimal preco) {
}
