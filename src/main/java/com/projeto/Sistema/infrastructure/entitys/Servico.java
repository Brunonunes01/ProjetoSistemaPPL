package com.projeto.Sistema.infrastructure.entitys;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "servico")
@Entity
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, unique = true)
    private String nome; // Ex: "Limpeza de Sofá 3 Lugares"

    @Column(name = "preco_venda", nullable = false)
    private BigDecimal precoVenda; // Ex: 250.00

    // --- Restrições (para o Otimizador - Módulo 3) ---
    // (Opcional) Demanda máxima por mês. (ex: x1 <= 50)
    @Column(name = "demanda_maxima")
    private Integer demandaMaxima;

    // (Opcional) Obrigação mínima (contrato). (ex: x1 >= 10)
    @Column(name = "demanda_minima")
    private Integer demandaMinima;

    // --- Relacionamento com a "Receita" ---
    // CascadeType.ALL: Se eu apagar o Servico, apague os gastos dele.
    // orphanRemoval=true: Se eu remover um Gasto da lista, apague ele do banco.
    @Builder.Default
    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GastoServico> gastos = new ArrayList<>();
}