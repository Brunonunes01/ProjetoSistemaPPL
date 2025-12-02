package com.projeto.Sistema.infrastructure.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class GastoServicoRequest {

    @NotNull(message = "Selecione um insumo.")
    private Long insumoId;

    @NotNull(message = "A quantidade é obrigatória.")
    @DecimalMin(value = "0.000001", message = "A quantidade deve ser maior que zero.")
    private BigDecimal quantidadeGasta;
}