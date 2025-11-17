package com.projeto.Sistema.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsumoResponse {
    private Long id;
    private String nome;
    private String unidadeDeMedida;
    private BigDecimal precoPorUnidade;
}