package com.projeto.Sistema.infrastructure.repository;

import com.projeto.Sistema.infrastructure.entitys.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {
}