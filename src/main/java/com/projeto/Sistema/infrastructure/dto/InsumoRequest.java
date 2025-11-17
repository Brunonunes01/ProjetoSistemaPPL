package com.projeto.Sistema.infrastructure.dto;

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
    private String nome;
    private String unidadeDeMedida; // Virá do <select>

    // --- CAMPOS ATUALIZADOS ---
    private BigDecimal precoRecipiente; // Ex: 130.00
    private BigDecimal quantidadeRecipiente; // Ex: 5

    // precoPorUnidade foi REMOVIDO daqui
}