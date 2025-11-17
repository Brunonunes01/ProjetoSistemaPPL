package com.projeto.Sistema.business;

import com.projeto.Sistema.infrastructure.dto.*;
import com.projeto.Sistema.infrastructure.entitys.GastoServico;
import com.projeto.Sistema.infrastructure.entitys.Insumo;
import com.projeto.Sistema.infrastructure.entitys.Servico;
import com.projeto.Sistema.infrastructure.repository.InsumoRepository;
import com.projeto.Sistema.infrastructure.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final InsumoRepository insumoRepository;
    // O GastoServicoRepository é gerenciado pelo ServicoRepository via Cascade

    // --- MÉTODOS DE CÁLCULO E CONVERSÃO (A MÁGICA) ---

    /**
     * Converte a Entidade (do Banco) para o DTO de Resposta (para a Tela)
     * É aqui que os custos e lucros são calculados em tempo real.
     */
    private ServicoResponse toResponse(Servico entity) {
        // 1. Mapeia a "receita" (gastos) e calcula o custo de cada item
        List<GastoServicoResponse> gastosResponse = entity.getGastos().stream().map(gasto -> {
            BigDecimal custoItem = gasto.getQuantidadeGasta()
                    .multiply(gasto.getInsumo().getPrecoPorUnidade())
                    .setScale(2, RoundingMode.HALF_UP);

            return GastoServicoResponse.builder()
                    .insumoNome(gasto.getInsumo().getNome())
                    .quantidadeGasta(gasto.getQuantidadeGasta())
                    .unidadeInsumo(gasto.getInsumo().getUnidadeDeMedida())
                    .precoInsumo(gasto.getInsumo().getPrecoPorUnidade())
                    .custoItem(custoItem)
                    .build();
        }).collect(Collectors.toList());

        // 2. Calcula o Custo Total somando o custo de cada item
        BigDecimal custoTotal = gastosResponse.stream()
                .map(GastoServicoResponse::getCustoItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Calcula o Lucro Unitário
        BigDecimal lucroUnitario = entity.getPrecoVenda().subtract(custoTotal);

        // 4. Monta a Resposta final
        return ServicoResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .precoVenda(entity.getPrecoVenda())
                .custoTotal(custoTotal)
                .lucroUnitario(lucroUnitario) // <- O dado para o Otimizador
                .gastos(gastosResponse)
                .demandaMaxima(entity.getDemandaMaxima())
                .demandaMinima(entity.getDemandaMinima())
                .build();
    }

    /**
     * Converte a Entidade (do Banco) para o DTO de Requisição (para o Form de Edição)
     */
    private ServicoRequest toRequest(Servico entity) {
        List<GastoServicoRequest> gastosRequest = entity.getGastos().stream().map(gasto -> {
            GastoServicoRequest item = new GastoServicoRequest();
            item.setInsumoId(gasto.getInsumo().getId());
            item.setQuantidadeGasta(gasto.getQuantidadeGasta());
            return item;
        }).collect(Collectors.toList());

        return ServicoRequest.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .precoVenda(entity.getPrecoVenda())
                .demandaMaxima(entity.getDemandaMaxima())
                .demandaMinima(entity.getDemandaMinima())
                .gastos(gastosRequest)
                .build();
    }

    // --- MÉTODOS DO CRUD (PÚBLICOS) ---

    // 1. LISTAR TODOS (READ)
    @Transactional(readOnly = true)
    public List<ServicoResponse> listarTodos() {
        // Usa a query otimizada para buscar tudo de uma vez
        return servicoRepository.findAllFetchGastosEInsumos().stream()
                .map(this::toResponse) // Calcula o lucro para cada um
                .collect(Collectors.toList());
    }

    // 2. BUSCAR POR ID (Para o Form de Edição)
    @Transactional(readOnly = true)
    public ServicoRequest buscarServicoPorId(Long id) {
        // Busca o serviço e seus gastos
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado para editar"));

        // Ativa o lazy-loading dos 'gastos' (se não tivessem sido 'fetched')
        // e converte para o DTO que o formulário espera
        return toRequest(servico);
    }

    // 3. SALVAR (CREATE)
    @Transactional
    public void salvarServico(ServicoRequest request) {
        // 1. Cria a entidade Servico (pai)
        Servico novoServico = Servico.builder()
                .nome(request.getNome())
                .precoVenda(request.getPrecoVenda())
                .demandaMaxima(request.getDemandaMaxima())
                .demandaMinima(request.getDemandaMinima())
                .build();

        // 2. Processa a "receita" (gastos)
        if (request.getGastos() != null && !request.getGastos().isEmpty()) {
            // Busca todos os Insumos necessários em UMA query
            List<Long> insumoIds = request.getGastos().stream()
                    .map(GastoServicoRequest::getInsumoId)
                    .collect(Collectors.toList());
            Map<Long, Insumo> insumosMap = insumoRepository.findAllById(insumoIds).stream()
                    .collect(Collectors.toMap(Insumo::getId, Function.identity()));

            // 3. Cria as entidades GastoServico (filhas)
            List<GastoServico> gastos = request.getGastos().stream().map(gastoDto -> {
                Insumo insumo = insumosMap.get(gastoDto.getInsumoId());
                if (insumo == null) {
                    throw new RuntimeException("Insumo ID " + gastoDto.getInsumoId() + " não encontrado.");
                }
                return GastoServico.builder()
                        .servico(novoServico) // Linka com o pai
                        .insumo(insumo)       // Linka com o Insumo
                        .quantidadeGasta(gastoDto.getQuantidadeGasta())
                        .build();
            }).collect(Collectors.toList());

            novoServico.setGastos(gastos);
        }

        // 4. Salva o Pai (e o JPA salva os Filhos por causa do CascadeType.ALL)
        servicoRepository.save(novoServico);
    }

    // 4. ATUALIZAR (UPDATE)
    @Transactional
    public void atualizarServico(Long id, ServicoRequest request) {
        // 1. Busca o Servico existente
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        // 2. Atualiza os campos simples
        servico.setNome(request.getNome());
        servico.setPrecoVenda(request.getPrecoVenda());
        servico.setDemandaMaxima(request.getDemandaMaxima());
        servico.setDemandaMinima(request.getDemandaMinima());

        // 3. Atualiza a "Receita" (GastoServico)
        // A forma mais robusta (Padrão Profissional):
        // Remove todos os gastos antigos (orphanRemoval=true) e adiciona os novos.
        servico.getGastos().clear();

        if (request.getGastos() != null && !request.getGastos().isEmpty()) {
            // Busca todos os Insumos necessários em UMA query
            List<Long> insumoIds = request.getGastos().stream()
                    .map(GastoServicoRequest::getInsumoId)
                    .collect(Collectors.toList());
            Map<Long, Insumo> insumosMap = insumoRepository.findAllById(insumoIds).stream()
                    .collect(Collectors.toMap(Insumo::getId, Function.identity()));

            // Adiciona os novos gastos
            List<GastoServico> novosGastos = request.getGastos().stream().map(gastoDto -> {
                Insumo insumo = insumosMap.get(gastoDto.getInsumoId());
                if (insumo == null) {
                    throw new RuntimeException("Insumo ID " + gastoDto.getInsumoId() + " não encontrado.");
                }
                return GastoServico.builder()
                        .servico(servico) // Linka com o pai
                        .insumo(insumo)   // Linka com o Insumo
                        .quantidadeGasta(gastoDto.getQuantidadeGasta())
                        .build();
            }).collect(Collectors.toList());

            servico.getGastos().addAll(novosGastos);
        }

        // 4. Salva o Servico (o JPA gerencia a atualização dos gastos)
        servicoRepository.save(servico);
    }

    // 5. DELETAR (DELETE)
    @Transactional
    public void deletarServicoPorId(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new RuntimeException("Serviço não encontrado para deletar");
        }
        // O CascadeType.ALL + orphanRemoval=true na entidade Servico
        // garante que, ao deletar o Servico, os GastoServico filhos
        // são deletados automaticamente.
        servicoRepository.deleteById(id);
    }
}