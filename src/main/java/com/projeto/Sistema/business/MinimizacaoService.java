package com.projeto.Sistema.business;

import com.projeto.Sistema.infrastructure.dto.MinimizacaoRequest;
import com.projeto.Sistema.infrastructure.dto.MinimizacaoResponse;
import com.projeto.Sistema.infrastructure.dto.ServicoResponse;
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
public class MinimizacaoService {

    private final ServicoService servicoService;

    @Transactional(readOnly = true)
    public MinimizacaoResponse resolverPPL(MinimizacaoRequest request) {

        // 1. BUSCAR DADOS: Pega todos os serviços e seus CUSTOS (do Módulo 2)
        List<ServicoResponse> servicos = servicoService.listarTodos();
        if (servicos.isEmpty()) {
            return MinimizacaoResponse.builder()
                    .mensagem("Erro: Nenhum serviço cadastrado.")
                    .build();
        }

        int numVariaveis = servicos.size(); // x1, x2, ...

        // 2. 💡 MONTAR FUNÇÃO OBJETIVO (Minimizar Z = C1*x1 + C2*x2 + ...)
        // Usamos o CUSTO TOTAL (custoTotal) em vez do LUCRO (lucroUnitario)
        double[] custos = servicos.stream()
                .mapToDouble(s -> s.getCustoTotal().doubleValue())
                .toArray();

        LinearObjectiveFunction f = new LinearObjectiveFunction(custos, 0);

        // 3. MONTAR RESTRIÇÕES (Sujeito a:)
        Collection<LinearConstraint> constraints = new ArrayList<>();

        // 3a. 💡 RESTRIÇÕES DE META (>= Meta Mínima)
        // O usuário informa as metas (ex: x1 >= 50)
        Map<Long, MinimizacaoRequest.MetaServico> metasMap = request.getMetas().stream()
                .collect(Collectors.toMap(MinimizacaoRequest.MetaServico::getServicoId, m -> m));

        for (int i = 0; i < numVariaveis; i++) {
            ServicoResponse servico = servicos.get(i);
            MinimizacaoRequest.MetaServico meta = metasMap.get(servico.getId());

            double metaMinima = (meta != null && meta.getMetaMinima() != null)
                    ? meta.getMetaMinima().doubleValue()
                    : 0.0; // Se não há meta, o mínimo é 0

            if (metaMinima > 0) {
                double[] constraint = new double[numVariaveis];
                constraint[i] = 1.0;
                // A restrição é "Maior ou Igual" (>=)
                constraints.add(new LinearConstraint(constraint, Relationship.GEQ, metaMinima));
            }
        }

        // 3b. Restrição de NÃO-NEGATIVIDADE (x_i >= 0)
        // (O solver cuida disso)

        // 4. 💡 RESOLVER O PROBLEMA (MINIMIZE)
        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution;
        try {
            solution = solver.optimize(
                    f,
                    new LinearConstraintSet(constraints),
                    GoalType.MINIMIZE, // 💡 O OBJETIVO MUDOU!
                    new NonNegativeConstraint(true)
            );
        } catch (NoFeasibleSolutionException e) {
            return MinimizacaoResponse.builder()
                    .mensagem("⚠️ Conflito de Metas: Não é possível atender a todas as metas mínimas simultaneamente " +
                            "com os recursos/restrições configurados.")
                    .build();
        } catch (UnboundedSolutionException e) {
            // Isso acontece se você não definir NENHUMA meta (custo mínimo é 0 fazendo 0 serviços)
            return MinimizacaoResponse.builder()
                    .mensagem("⚠️ Nenhuma meta definida: Para minimizar custos, o sistema precisa saber o que você " +
                            "é obrigado a produzir. Insira pelo menos uma meta maior que zero.")
                    .build();
        }

        // 5. MONTAR A RESPOSTA
        BigDecimal custoMinimo = BigDecimal.valueOf(solution.getValue())
                .setScale(2, RoundingMode.HALF_UP);

        double[] quantidades = solution.getPoint();

        List<MinimizacaoResponse.ResultadoServicoMin> planoIdeal = IntStream.range(0, numVariaveis)
                .mapToObj(i -> MinimizacaoResponse.ResultadoServicoMin.builder()
                        .nomeServico(servicos.get(i).getNome())
                        .quantidade(Math.round(quantidades[i])) // Arredonda
                        .build())
                .filter(r -> r.getQuantidade() > 0) // Só mostra o que deve ser feito
                .collect(Collectors.toList());

        return MinimizacaoResponse.builder()
                .custoMinimoTotal(custoMinimo)
                .planoIdeal(planoIdeal)
                .mensagem("Solução de custo mínimo encontrada!")
                .build();
    }
}