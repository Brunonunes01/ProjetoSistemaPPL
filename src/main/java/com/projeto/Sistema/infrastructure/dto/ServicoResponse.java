package com.projeto.Sistema.infrastructure.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

// DTO para a "Tela 1" (Dashboard de Serviços)
// É o DTO "rico" que mostra os cálculos.
@Data
@Builder
public class ServicoResponse {
    private Long id;
    private String nome;
    private BigDecimal precoVenda;

    // --- Campos Calculados ---
    private BigDecimal custoTotal;
    private BigDecimal lucroUnitario; // <-- O que o Otimizador vai usar!

    // A receita detalhada (para mostrar se o usuário expandir)
    private List<GastoServicoResponse> gastos;

    // Restrições (para o Otimizador)
    private Integer demandaMaxima;
    private Integer demandaMinima;
}