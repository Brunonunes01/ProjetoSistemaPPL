package com.projeto.Sistema.infrastructure.repository;

import com.projeto.Sistema.infrastructure.entitys.GastoServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GastoServicoRepository extends JpaRepository<GastoServico, Long> {
    // Este repositório é usado internamente, mas é bom tê-lo.
}