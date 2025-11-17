package com.projeto.Sistema.controller;

import com.projeto.Sistema.business.MinimizacaoService;
import com.projeto.Sistema.business.ServicoService;
import com.projeto.Sistema.infrastructure.dto.MinimizacaoRequest;
import com.projeto.Sistema.infrastructure.dto.MinimizacaoResponse;
import com.projeto.Sistema.infrastructure.dto.ServicoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MinimizacaoWebController {

    private final MinimizacaoService minimizacaoService;
    private final ServicoService servicoService; // Para buscar os serviços

    @GetMapping("/minimizador")
    public String mostrarPaginaMinimizador(Model model) {

        // 1. Pega todos os serviços cadastrados (Módulo 2)
        List<ServicoResponse> servicos = servicoService.listarTodos();

        // 2. Prepara o formulário (Request)
        MinimizacaoRequest requestForm = new MinimizacaoRequest();

        // 3. Pre-popula o formulário com os IDs dos serviços
        requestForm.setMetas(servicos.stream().map(servico -> {
            MinimizacaoRequest.MetaServico meta = new MinimizacaoRequest.MetaServico();
            meta.setServicoId(servico.getId());
            return meta;
        }).toList());

        model.addAttribute("requestForm", requestForm);
        model.addAttribute("servicos", servicos); // Lista de serviços para mostrar nomes
        model.addAttribute("titulo", "Minimizador de Custo (Metas)");

        return "minimizador"; // -> templates/minimizador.html
    }

    // 2. EXECUTAR A OTIMIZAÇÃO
    @PostMapping("/minimizar")
    public String executarMinimizacao(
            @ModelAttribute("requestForm") MinimizacaoRequest request,
            Model model) {

        // 1. Chama o "Cérebro Solver" (o novo service)
        MinimizacaoResponse resposta = minimizacaoService.resolverPPL(request);

        // 2. Devolve os resultados para a mesma página
        model.addAttribute("resposta", resposta); // O RESULTADO
        model.addAttribute("requestForm", request); // O FORMULÁRIO (preenchido)

        // 3. Devolve os dados de base novamente
        model.addAttribute("servicos", servicoService.listarTodos());
        model.addAttribute("titulo", "Resultado da Minimização");

        return "minimizador"; // Volta para templates/minimizador.html
    }
}