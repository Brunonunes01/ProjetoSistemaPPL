package com.projeto.Sistema.infrastructure.dto;

import lombok.Data;
import java.math.BigDecimal;

// DTO para o formulário.
@Data
public class GastoServicoRequest {
    private Long insumoId; // O ID do Insumo selecionado (ex: 1)
    private BigDecimal quantidadeGasta; // A quantidade gasta (ex: 0.150)
}