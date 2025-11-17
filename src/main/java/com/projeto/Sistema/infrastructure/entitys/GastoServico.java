package com.projeto.Sistema.infrastructure.entitys;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Esta é a "Tabela de Junção" que forma a "Receita".
 * Ela diz: 1 'Servico' gasta X 'quantidade' de 1 'Insumo'.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "gasto_servico")
@Entity
public class GastoServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muitos Gastos podem pertencer a 1 Servico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    // Muitos Gastos podem se referir a 1 Insumo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    // Quantidade gasta (ex: 0.150)
    // DEVE estar na mesma unidade de medida do Insumo (ex: Litros)
    @Column(name = "quantidade_gasta", nullable = false)
    private BigDecimal quantidadeGasta;
}