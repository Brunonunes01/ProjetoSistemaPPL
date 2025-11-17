package com.projeto.Sistema.infrastructure.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

// A resposta que o sistema vai dar
@Data
@Builder
public class MinimizacaoResponse {

    private BigDecimal custoMinimoTotal; // Ex: "R$ 5.000,00"
    private List<ResultadoServicoMin> planoIdeal;
    private String mensagem;

    @Data
    @Builder
    public static class ResultadoServicoMin {
        private String nomeServico;
        private long quantidade; // O solver pode retornar "50.0001", arredondamos
    }
}