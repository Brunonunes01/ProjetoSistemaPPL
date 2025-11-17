package com.projeto.Sistema.infrastructure.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

// DTO para exibir a receita (o que foi gasto)
@Data
@Builder
public class GastoServicoResponse {
    private String insumoNome; // Ex: "Produto Limpador X"
    private BigDecimal quantidadeGasta; // Ex: 0.150
    private String unidadeInsumo; // Ex: "Litro"
    private BigDecimal precoInsumo; // Ex: 120.00
    private BigDecimal custoItem; // Custo (Calculado) = 0.150 * 120.00
}