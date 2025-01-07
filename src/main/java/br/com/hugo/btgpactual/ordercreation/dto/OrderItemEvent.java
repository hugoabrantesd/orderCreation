package br.com.hugo.btgpactual.ordercreation.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemEvent(String produto, Integer quantidade, BigDecimal preco) {
}
