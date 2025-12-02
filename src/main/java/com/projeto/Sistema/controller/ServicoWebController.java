package com.projeto.Sistema.controller;

import com.projeto.Sistema.business.InsumoService;
import com.projeto.Sistema.business.ServicoService;
import com.projeto.Sistema.infrastructure.dto.InsumoResponse;
import com.projeto.Sistema.infrastructure.dto.ServicoRequest;
import com.projeto.Sistema.infrastructure.dto.ServicoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ServicoWebController {

    private final ServicoService servicoService;
    private final InsumoService insumoService; // 💡 Precisamos dele para o form!

    // --- LISTAR (READ) ---
    // Esta é a sua "Tela 1" (Dashboard de Serviços)
    @GetMapping("/servicos")
    public String listarServicos(Model model) {
        List<ServicoResponse> servicos = servicoService.listarTodos();
        model.addAttribute("servicos", servicos);
        model.addAttribute("titulo", "Dashboard de Serviços e Lucratividade");
        return "lista-servicos"; // -> templates/lista-servicos.html
    }

    // --- MOSTRAR FORM DE CADASTRO (CREATE) ---
    @GetMapping("/servicos/novo")
    public String mostrarFormularioDeCadastro(Model model) {
        // Envia um DTO de Serviço vazio para o formulário
        model.addAttribute("servico", new ServicoRequest());

        // 💡 Envia a lista de TODOS os insumos para o dropdown do form
        List<InsumoResponse> insumos = insumoService.listarTodos();
        model.addAttribute("insumos", insumos);

        model.addAttribute("titulo", "Cadastrar Novo Serviço");
        return "form-servico"; // -> templates/form-servico.html
    }

    // --- MOSTRAR FORM DE EDIÇÃO (UPDATE P-1) ---
    @GetMapping("/servicos/editar/{id}")
    public String mostrarFormularioDeEdicao(@PathVariable Long id, Model model) {
        // 1. Busca o DTO do Serviço (já preenchido com os gastos)
        ServicoRequest servicoDto = servicoService.buscarServicoPorId(id);
        model.addAttribute("servico", servicoDto);

        // 2. 💡 Envia a lista de TODOS os insumos para o dropdown
        List<InsumoResponse> insumos = insumoService.listarTodos();
        model.addAttribute("insumos", insumos);

        model.addAttribute("titulo", "Editar Serviço (ID: " + id + ")");
        return "form-servico"; // Reutiliza o mesmo formulário
    }

    // --- EXCLUIR (DELETE) ---
    @GetMapping("/servicos/excluir/{id}")
    public String excluirServico(@PathVariable Long id) {
        servicoService.deletarServicoPorId(id);
        return "redirect:/servicos"; // Volta para a lista de serviços
    }

    // --- SALVAR (CREATE OU UPDATE P-2) ---
    @PostMapping("/servicos/salvar")
    public String salvarServico(@ModelAttribute("servico") ServicoRequest request, Model model) {
        try {
            if (request.getId() == null) {
                // Se o ID é NULO, é um serviço novo (CREATE)
                servicoService.salvarServico(request);
            } else {
                // Se o ID NÃO é nulo, é um serviço existente (UPDATE)
                servicoService.atualizarServico(request.getId(), request);
            }
        } catch (DataIntegrityViolationException e) {
            // Trata caso tente criar um serviço com nome duplicado (já que o nome é unique=true no banco)
            model.addAttribute("erro", "Já existe um serviço cadastrado com o nome '" + request.getNome() + "'. Por favor, escolha outro nome.");

            // Recarrega os dados para a tela não quebrar
            model.addAttribute("servico", request);
            model.addAttribute("insumos", insumoService.listarTodos());
            model.addAttribute("titulo", "Erro ao Salvar Serviço");
            return "form-servico";

        } catch (Exception e) {
            // Em caso de erro genérico (ex: Insumo não encontrado)
            model.addAttribute("erro", "Ocorreu um erro: " + e.getMessage());
            model.addAttribute("servico", request); // Devolve o DTO preenchido
            model.addAttribute("insumos", insumoService.listarTodos()); // Envia os insumos de novo
            model.addAttribute("titulo", "Erro ao Salvar Serviço");
            return "form-servico";
        }

        return "redirect:/servicos"; // Volta para a lista de serviços
    }
}