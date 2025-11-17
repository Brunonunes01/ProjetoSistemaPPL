package com.projeto.Sistema.infrastructure.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "insumo")
@Entity
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "unidade_medida", nullable = false)
    private String unidadeDeMedida; // Ex: "Litro", "kWh", "Hora"

    // --- NOVOS CAMPOS PARA O CÁLCULO ---
    // O preço que você pagou no "galão" (ex: 130.00)
    @Column(name = "preco_recipiente", nullable = false)
    private BigDecimal precoRecipiente;

    // A quantidade que veio no "galão" (ex: 5)
    @Column(name = "quantidade_recipiente", nullable = false)
    private BigDecimal quantidadeRecipiente;

    // --- CAMPO CALCULADO (O SISTEMA PREENCHE SOZINHO) ---
    // O Módulo 2 (Serviços) usará este campo.
    @Column(name = "preco_por_unidade", nullable = false)
    private BigDecimal precoPorUnidade; // Ex: 26.00 (130 / 5)


    // 💡 --- LÓGICA DE CÁLCULO AUTOMÁTICO --- 💡
    /**
     * Este método é chamado pelo JPA automaticamente
     * ANTES de salvar (Persist) ou atualizar (Update) no banco.
     */
    @PrePersist
    @PreUpdate
    public void calcularPrecoPorUnidade() {
        if (precoRecipiente == null || quantidadeRecipiente == null) {
            throw new IllegalStateException("Preço e quantidade do recipiente não podem ser nulos.");
        }
        if (quantidadeRecipiente.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Quantidade do recipiente não pode ser zero.");
        }

        // A MÁGICA: Divide o preço pela quantidade (ex: 130 / 5 = 26)
        // Usamos 4 casas decimais para alta precisão (ex: R$ 0,009 por ml)
        this.precoPorUnidade = precoRecipiente.divide(
                quantidadeRecipiente,
                4, // 4 casas de precisão
                RoundingMode.HALF_UP
        );
    }
}