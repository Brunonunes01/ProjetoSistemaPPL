package com.projeto.Sistema.infrastructure.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

// O formulário que o usuário vai preencher
@Data
public class MinimizacaoRequest {
    private List<MetaServico> metas;

    @Data
    public static class MetaServico {
        private Long servicoId;
        private BigDecimal metaMinima; // Quero fazer "50" sofás
    }
}