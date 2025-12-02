package com.projeto.Sistema.infrastructure.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsumoRequest {

    private Long id;

    @NotBlank(message = "O nome do insumo é obrigatório.")
    private String nome;

    @NotBlank(message = "Selecione uma unidade de medida.")
    private String unidadeDeMedida;

    @NotNull(message = "O preço do recipiente é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    private BigDecimal precoRecipiente; // Ex: 130.00

    @NotNull(message = "A quantidade no recipiente é obrigatória.")
    @DecimalMin(value = "0.0001", message = "A quantidade deve ser maior que zero.")
    private BigDecimal quantidadeRecipiente; // Ex: 5
}