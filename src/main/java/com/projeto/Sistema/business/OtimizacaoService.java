package com.projeto.Sistema.business;

import com.projeto.Sistema.infrastructure.dto.OtimizacaoRequest;
import com.projeto.Sistema.infrastructure.dto.OtimizacaoResponse;
import com.projeto.Sistema.infrastructure.dto.ServicoResponse;
import com.projeto.Sistema.infrastructure.entitys.Insumo;
import com.projeto.Sistema.infrastructure.repository.InsumoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OtimizacaoService {

    // Precisamos dos outros services para buscar os dados de base
    private final ServicoService servicoService;
    private final InsumoRepository insumoRepository;

    @Transactional(readOnly = true)
    public OtimizacaoResponse resolverPPL(OtimizacaoRequest request) {

        // 1. BUSCAR DADOS: Pega todos os serviços e seus lucros (do Módulo 2)
        List<ServicoResponse> servicos = servicoService.listarTodos();
        if (servicos.isEmpty()) {
            return OtimizacaoResponse.builder()
                    .mensagem("Erro: Nenhum serviço cadastrado para otimizar.")
                    .build();
        }

        int numVariaveis = servicos.size(); // Nossas variáveis (x1, x2, ...)

        // 2. MONTAR FUNÇÃO OBJETIVO (Maximizar Z = L1*x1 + L2*x2 + ...)
        // Pega o Lucro Unitário de cada serviço.
        double[] lucros = servicos.stream()
                .mapToDouble(s -> s.getLucroUnitario().doubleValue())
                .toArray();

        LinearObjectiveFunction f = new LinearObjectiveFunction(lucros, 0); // 0 = valor inicial

        // 3. MONTAR RESTRIÇÕES (Sujeito a:)
        Collection<LinearConstraint> constraints = new ArrayList<>();

        // 3a. Restrições de INSUMOS (<= Limite Disponível)
        // (ex: 0.15*x1 + 0.1*x2 <= 10 Litros)
        Map<Long, OtimizacaoRequest.LimiteInsumo> limitesMap = request.getLimites().stream()
                .collect(Collectors.toMap(OtimizacaoRequest.LimiteInsumo::getInsumoId, l -> l));

        List<Insumo> todosInsumos = insumoRepository.findAll();

        for (Insumo insumo : todosInsumos) {
            double[] gastosDoInsumo = new double[numVariaveis];

            for (int i = 0; i < numVariaveis; i++) {
                ServicoResponse servico = servicos.get(i);
                // Encontra quanto o serviço 'i' gasta deste 'insumo'
                double gasto = servico.getGastos().stream()
                        .filter(g -> g.getInsumoNome().equals(insumo.getNome()))
                        .map(g -> g.getQuantidadeGasta().doubleValue())
                        .findFirst()
                        .orElse(0.0); // Se não gasta, é 0

                gastosDoInsumo[i] = gasto;
            }

            // Pega o limite que o usuário digitou para este insumo
            OtimizacaoRequest.LimiteInsumo limite = limitesMap.get(insumo.getId());
            double totalDisponivel = (limite != null && limite.getTotalDisponivel() != null)
                    ? limite.getTotalDisponivel().doubleValue()
                    : Double.MAX_VALUE; // Se não limitou, é "infinito"

            if (totalDisponivel < Double.MAX_VALUE) {
                constraints.add(new LinearConstraint(gastosDoInsumo, Relationship.LEQ, totalDisponivel));
            }
        }

        // 3b. Restrições de DEMANDA (Mínima e Máxima)
        for (int i = 0; i < numVariaveis; i++) {
            ServicoResponse servico = servicos.get(i);

            // Demanda MÁXIMA (ex: x1 <= 50)
            if (servico.getDemandaMaxima() != null && servico.getDemandaMaxima() > 0) {
                double[] constraint = new double[numVariaveis];
                constraint[i] = 1.0;
                constraints.add(new LinearConstraint(constraint, Relationship.LEQ, servico.getDemandaMaxima()));
            }

            // Demanda MÍNIMA (ex: x2 >= 10)
            if (servico.getDemandaMinima() != null && servico.getDemandaMinima() > 0) {
                double[] constraint = new double[numVariaveis];
                constraint[i] = 1.0;
                constraints.add(new LinearConstraint(constraint, Relationship.GEQ, servico.getDemandaMinima()));
            }
        }

        // 3c. Restrição de NÃO-NEGATIVIDADE (x_i >= 0)
        // O `NonNegativeConstraint(true)` cuida disso.

        // 4. RESOLVER O PROBLEMA
        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution;
        try {
            solution = solver.optimize(
                    f,
                    new LinearConstraintSet(constraints),
                    GoalType.MAXIMIZE,
                    new NonNegativeConstraint(true) // Garante que x_i >= 0
            );
        } catch (NoFeasibleSolutionException e) {
            return OtimizacaoResponse.builder()
                    .mensagem("⚠️ Não foi possível encontrar uma solução: As restrições são muito rígidas. " +
                            "Tente aumentar a disponibilidade de insumos ou reduzir as demandas mínimas.")
                    .build();
        } catch (UnboundedSolutionException e) {
            return OtimizacaoResponse.builder()
                    .mensagem("⚠️ Erro de configuração: O lucro parece ser infinito. " +
                            "Verifique se todos os serviços possuem custos cadastrados (insumos) " +
                            "e se o preço de venda está realista.")
                    .build();
        }

        // 5. MONTAR A RESPOSTA
        BigDecimal lucroMaximo = BigDecimal.valueOf(solution.getValue())
                .setScale(2, RoundingMode.HALF_UP);

        double[] quantidades = solution.getPoint();

        List<OtimizacaoResponse.ResultadoServico> planoIdeal = IntStream.range(0, numVariaveis)
                .mapToObj(i -> OtimizacaoResponse.ResultadoServico.builder()
                        .nomeServico(servicos.get(i).getNome())
                        .quantidade(quantidades[i])
                        .quantidadeArredondada(Math.round(quantidades[i]))
                        .build())
                .filter(r -> r.getQuantidadeArredondada() > 0) // Só mostra o que deve ser feito
                .collect(Collectors.toList());

        return OtimizacaoResponse.builder()
                .lucroMaximo(lucroMaximo)
                .planoIdeal(planoIdeal)
                .mensagem("Solução ótima encontrada!")
                .build();
    }
}