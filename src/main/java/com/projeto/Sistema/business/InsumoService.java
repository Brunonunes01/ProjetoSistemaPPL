package com.projeto.Sistema.business;

import com.projeto.Sistema.infrastructure.dto.InsumoRequest;
import com.projeto.Sistema.infrastructure.dto.InsumoResponse;
import com.projeto.Sistema.infrastructure.entitys.Insumo;
import com.projeto.Sistema.infrastructure.repository.InsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InsumoService {

    private final InsumoRepository repository;

    // toResponse (Listar) não muda. Ele continua mostrando o
    // precoPorUnidade calculado, o que é PERFEITO para o usuário validar.
    private InsumoResponse toResponse(Insumo entity) {
        return InsumoResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .unidadeDeMedida(entity.getUnidadeDeMedida())
                .precoPorUnidade(entity.getPrecoPorUnidade()) // <- Mostra o R$ 26/Litro
                .build();
    }

    // toRequest (para Editar) agora retorna os campos do recipiente
    private InsumoRequest toRequest(Insumo entity) {
        return InsumoRequest.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .unidadeDeMedida(entity.getUnidadeDeMedida())
                .precoRecipiente(entity.getPrecoRecipiente()) // <- NOVO
                .quantidadeRecipiente(entity.getQuantidadeRecipiente()) // <- NOVO
                .build();
    }

    // --- Métodos do CRUD ATUALIZADOS ---

    public void salvarInsumo(InsumoRequest request) {
        Insumo novoInsumo = Insumo.builder()
                .nome(request.getNome())
                .unidadeDeMedida(request.getUnidadeDeMedida())
                .precoRecipiente(request.getPrecoRecipiente()) // <- NOVO
                .quantidadeRecipiente(request.getQuantidadeRecipiente()) // <- NOVO
                .build();
        // O @PrePersist da Entidade vai calcular o precoPorUnidade
        repository.save(novoInsumo);
    }

    public void atualizarInsumo(Long id, InsumoRequest request) {
        Insumo insumoEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insumo não encontrado"));

        insumoEntity.setNome(request.getNome());
        insumoEntity.setUnidadeDeMedida(request.getUnidadeDeMedida());
        insumoEntity.setPrecoRecipiente(request.getPrecoRecipiente()); // <- NOVO
        insumoEntity.setQuantidadeRecipiente(request.getQuantidadeRecipiente()); // <- NOVO

        // O @PreUpdate da Entidade vai RE-calcular o precoPorUnidade
        repository.save(insumoEntity);
    }

    // O resto (listarTodos, deletarInsumoPorId) continua igual.
    public List<InsumoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public InsumoRequest buscarInsumoPorId(Long id) {
        Insumo insumoEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insumo não encontrado para editar"));
        return toRequest(insumoEntity);
    }

    public void deletarInsumoPorId(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Insumo não encontrado para deletar");
        }
        repository.deleteById(id);
    }
}