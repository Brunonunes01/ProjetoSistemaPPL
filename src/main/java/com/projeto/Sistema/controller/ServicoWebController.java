package com.projeto.Sistema.controller;

import com.projeto.Sistema.business.InsumoService;
import com.projeto.Sistema.business.ServicoService;
import com.projeto.Sistema.infrastructure.dto.InsumoResponse;
import com.projeto.Sistema.infrastructure.dto.ServicoRequest;
import com.projeto.Sistema.infrastructure.dto.ServicoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ServicoWebController {

    private final ServicoService servicoService;
    private final InsumoService insumoService;

    // --- LISTAR (READ) ---
    @GetMapping("/servicos")
    public String listarServicos(Model model) {
        List<ServicoResponse> servicos = servicoService.listarTodos();
        model.addAttribute("servicos", servicos);
        model.addAttribute("titulo", "Dashboard de Serviços e Lucratividade");
        return "lista-servicos";
    }

    // --- MOSTRAR FORM DE CADASTRO (CREATE) ---
    @GetMapping("/servicos/novo")
    public String mostrarFormularioDeCadastro(Model model) {
        model.addAttribute("servico", new ServicoRequest());
        model.addAttribute("insumos", insumoService.listarTodos());
        model.addAttribute("titulo", "Cadastrar Novo Serviço");
        return "form-servico";
    }

    // --- MOSTRAR FORM DE EDIÇÃO (UPDATE P-1) ---
    @GetMapping("/servicos/editar/{id}")
    public String mostrarFormularioDeEdicao(@PathVariable Long id, Model model) {
        ServicoRequest servicoDto = servicoService.buscarServicoPorId(id);
        model.addAttribute("servico", servicoDto);
        model.addAttribute("insumos", insumoService.listarTodos());
        model.addAttribute("titulo", "Editar Serviço (ID: " + id + ")");
        return "form-servico";
    }

    // --- EXCLUIR (DELETE) ---
    @GetMapping("/servicos/excluir/{id}")
    public String excluirServico(@PathVariable Long id) {
        servicoService.deletarServicoPorId(id);
        return "redirect:/servicos";
    }

    // --- SALVAR (CREATE OU UPDATE P-2) ---
    @PostMapping("/servicos/salvar")
    public String salvarServico(@Valid @ModelAttribute("servico") ServicoRequest request,
                                BindingResult bindingResult,
                                Model model) {

        // 1. Validação de campos (Nome vazio, Preço negativo, Lista vazia)
        if (bindingResult.hasErrors()) {
            model.addAttribute("insumos", insumoService.listarTodos());
            model.addAttribute("titulo", request.getId() == null ? "Cadastrar Novo Serviço" : "Editar Serviço");
            model.addAttribute("erro", "Verifique os campos destacados abaixo.");
            return "form-servico";
        }

        try {
            if (request.getId() == null) {
                servicoService.salvarServico(request);
            } else {
                servicoService.atualizarServico(request.getId(), request);
            }
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("erro", "Já existe um serviço com o nome '" + request.getNome() + "'.");
            model.addAttribute("insumos", insumoService.listarTodos());
            model.addAttribute("titulo", "Erro ao Salvar");
            return "form-servico";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro inesperado: " + e.getMessage());
            model.addAttribute("insumos", insumoService.listarTodos());
            model.addAttribute("titulo", "Erro ao Salvar");
            return "form-servico";
        }

        return "redirect:/servicos";
    }
}