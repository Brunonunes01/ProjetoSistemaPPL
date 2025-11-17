package com.projeto.Sistema.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

// DTO para o formulário (CREATE e UPDATE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoRequest {
    private Long id;
    private String nome;
    private BigDecimal precoVenda;
    private Integer demandaMaxima;
    private Integer demandaMinima;

    // Um Serviço é composto por uma lista da "receita"
    private List<GastoServicoRequest> gastos;
}