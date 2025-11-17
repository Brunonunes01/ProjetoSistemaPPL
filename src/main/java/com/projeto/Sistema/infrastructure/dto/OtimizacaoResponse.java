package com.projeto.Sistema.infrastructure.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

// Este DTO representa o resultado (a solução)
@Data
@Builder
public class OtimizacaoResponse {

    // O valor máximo da Função Objetivo (ex: "R$ 8.500,00")
    private BigDecimal lucroMaximo;

    // A lista de quantos serviços devem ser feitos
    private List<ResultadoServico> planoIdeal;

    // Opcional: Mensagem de status (ex: "Solução ótima encontrada")
    private String mensagem;

    @Data
    @Builder
    public static class ResultadoServico {
        private String nomeServico;
        private double quantidade; // A quantidade ideal (ex: 10.5)
        private long quantidadeArredondada; // ex: 10
    }
}