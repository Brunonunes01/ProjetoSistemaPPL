package com.projeto.Sistema.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoRequest {

    private Long id;

    @NotBlank(message = "O nome do serviço é obrigatório.")
    private String nome;

    @NotNull(message = "O preço de venda é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    private BigDecimal precoVenda;

    // --- Estes campos continuam OPCIONAIS (sem @NotNull) ---
    private Integer demandaMaxima;
    private Integer demandaMinima;

    @NotEmpty(message = "O serviço precisa ter pelo menos 1 insumo na receita.")
    @Valid // Valida cada item da lista (GastoServicoRequest)
    private List<GastoServicoRequest> gastos;
}