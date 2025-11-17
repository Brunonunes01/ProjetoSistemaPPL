package com.projeto.Sistema.infrastructure.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

// Este DTO representa o formulário que o usuário vai preencher
@Data
public class OtimizacaoRequest {

    // O formulário enviará uma lista de limites de insumos
    private List<LimiteInsumo> limites;

    @Data
    public static class LimiteInsumo {
        // O ID do Insumo (ex: 1 para "Mão de Obra", 2 para "Produto X")
        private Long insumoId;

        // A quantidade total disponível (ex: 160 horas, 10 Litros)
        private BigDecimal totalDisponivel;
    }
}