package com.projeto.Sistema.controller;

import com.projeto.Sistema.business.InsumoService;
import com.projeto.Sistema.business.OtimizacaoService;
import com.projeto.Sistema.infrastructure.dto.InsumoResponse;
import com.projeto.Sistema.infrastructure.dto.OtimizacaoRequest;
import com.projeto.Sistema.infrastructure.dto.OtimizacaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OtimizacaoWebController {

    private final OtimizacaoService otimizacaoService;
    private final InsumoService insumoService;

    // 1. MOSTRAR A PÁGINA
    @GetMapping("/otimizador")
    public String mostrarPaginaOtimizador(Model model) {

        // 1. Pega todos os insumos cadastrados (Módulo 1)
        List<InsumoResponse> insumos = insumoService.listarTodos();

        // 2. Prepara o formulário (Request) que o usuário vai preencher
        OtimizacaoRequest requestForm = new OtimizacaoRequest();

        // 3. Pre-popula o formulário com os IDs dos insumos
        requestForm.setLimites(insumos.stream().map(insumo -> {
            OtimizacaoRequest.LimiteInsumo limite = new OtimizacaoRequest.LimiteInsumo();
            limite.setInsumoId(insumo.getId());
            return limite;
        }).toList());

        model.addAttribute("requestForm", requestForm);
        model.addAttribute("insumos", insumos); // Lista de insumos para mostrar nomes
        model.addAttribute("titulo", "Otimizador de Lucro");

        return "otimizador"; // -> templates/otimizador.html
    }

    // 2. EXECUTAR A OTIMIZAÇÃO
    @PostMapping("/otimizar")
    public String executarOtimizacao(
            @ModelAttribute("requestForm") OtimizacaoRequest request,
            Model model) {

        // 1. Chama o "Cérebro Solver"
        OtimizacaoResponse resposta = otimizacaoService.resolverPPL(request);

        // 2. Devolve os resultados para a mesma página
        model.addAttribute("resposta", resposta); // O RESULTADO
        model.addAttribute("requestForm", request); // O FORMULÁRIO (preenchido)

        // 3. Devolve os dados de base novamente
        model.addAttribute("insumos", insumoService.listarTodos());
        model.addAttribute("titulo", "Resultado da Otimização");

        return "otimizador"; // Volta para templates/otimizador.html
    }
}