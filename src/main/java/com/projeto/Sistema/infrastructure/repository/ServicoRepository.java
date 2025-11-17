package com.projeto.Sistema.infrastructure.repository;

import com.projeto.Sistema.infrastructure.entitys.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    // Otimização: Pede ao JPA para buscar os Serviços e já "anexar" (join)
    // os Gastos e Insumos relacionados em uma única consulta ao banco,
    // evitando o problema de N+1 queries (consulta N+1).
    @Query("SELECT s FROM Servico s LEFT JOIN FETCH s.gastos g LEFT JOIN FETCH g.insumo")
    List<Servico> findAllFetchGastosEInsumos();
}