package com.projeto.Sistema.controller;

// Imports dos nossos pacotes
import com.projeto.Sistema.business.InsumoService;
import com.projeto.Sistema.infrastructure.dto.InsumoRequest;
import com.projeto.Sistema.infrastructure.dto.InsumoResponse;

// Import do Lombok
import lombok.RequiredArgsConstructor;

// Imports do Spring
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// Imports do Java (para a lista de unidades)
import java.util.Arrays;
import java.util.List;

/**
 * Controlador Web para gerenciar o CRUD (Criar, Ler, Atualizar, Deletar)
 * da entidade Insumo.
 */
@Controller
@RequiredArgsConstructor
public class InsumoWebController {

    private final InsumoService insumoService;

    /**
     * Define a lista de unidades de medida disponíveis no sistema.
     * Esta lista é usada para popular o dropdown no formulário.
     */
    private List<String> getUnidadesDeMedida() {
        // 💡 LISTA ATUALIZADA (como você pediu)
        return Arrays.asList(
                // Líquidos
                "Litro",
                "ml",
                // Peso
                "kg",
                "g",
                // Distância
                "km",
                // Volume (para água/gás)
                "m³ (metro cúbico)",
                // Tempo
                "Hora",
                "Minuto",
                // Energia
                "kWh",
                // Genérico
                "Unidade(s)"
        );
    }

    // --- LISTAR (READ) ---
    /**
     * Mapeia a rota "/insumos" (GET)
     * Busca todos os insumos e os exibe na tela "lista-insumos.html".
     */
    @GetMapping("/insumos")
    public String listarInsumos(Model model) {
        List<InsumoResponse> insumos = insumoService.listarTodos();
        model.addAttribute("insumos", insumos);
        model.addAttribute("titulo", "Lista de Insumos (Custos Variáveis)");
        return "lista-insumos";
    }

    // --- MOSTRAR FORM DE CADASTRO (CREATE) ---
    /**
     * Mapeia a rota "/insumos/novo" (GET)
     * Prepara um formulário vazio para cadastrar um novo insumo.
     * Envia a lista de unidades para o formulário.
     */
    @GetMapping("/insumos/novo")
    public String mostrarFormularioDeCadastro(Model model) {
        model.addAttribute("insumo", new InsumoRequest());
        model.addAttribute("titulo", "Cadastrar Novo Insumo");
        model.addAttribute("unidades", getUnidadesDeMedida()); // Envia a lista de unidades
        return "form-insumo";
    }

    // --- MOSTRAR FORM DE EDIÇÃO (UPDATE P-1) ---
    /**
     * Mapeia a rota "/insumos/editar/{id}" (GET)
     * Busca um insumo pelo ID e preenche o formulário para edição.
     * Envia a lista de unidades para o formulário.
     */
    @GetMapping("/insumos/editar/{id}")
    public String mostrarFormularioDeEdicao(@PathVariable Long id, Model model) {
        InsumoRequest insumoDto = insumoService.buscarInsumoPorId(id);
        model.addAttribute("insumo", insumoDto);
        model.addAttribute("titulo", "Editar Insumo (ID: " + id + ")");
        model.addAttribute("unidades", getUnidadesDeMedida()); // Envia a lista de unidades
        return "form-insumo";
    }

    // --- EXCLUIR (DELETE) ---
    /**
     * Mapeia a rota "/insumos/excluir/{id}" (GET)
     * Deleta um insumo pelo ID e redireciona de volta para a lista.
     */
    @GetMapping("/insumos/excluir/{id}")
    public String excluirInsumo(@PathVariable Long id) {
        insumoService.deletarInsumoPorId(id);
        return "redirect:/insumos";
    }

    // --- SALVAR (CREATE OU UPDATE P-2) ---
    /**
     * Mapeia a rota "/insumos/salvar" (POST)
     * Recebe os dados do formulário (seja novo ou edição) e salva no banco.
     * Redireciona de volta para a lista.
     */
    @PostMapping("/insumos/salvar")
    public String salvarInsumo(@ModelAttribute("insumo") InsumoRequest request) {
        if (request.getId() == null) {
            // Se o ID é NULO, é um insumo novo (CREATE)
            insumoService.salvarInsumo(request);
        } else {
            // Se o ID NÃO é nulo, é um insumo existente (UPDATE)
            insumoService.atualizarInsumo(request.getId(), request);
        }
        return "redirect:/insumos";
    }

    // --- REDIRECIONAMENTO DA RAIZ ---
    /**
     * Mapeia a rota raiz "/" (GET)
     * Redireciona o usuário da página inicial direto para a lista de insumos.
     * (Corrige o erro 404 da página inicial).
     */
    @GetMapping("/")
    public String redirecionarParaServicos() { // Mudei o nome do método
        return "redirect:/servicos"; // Mudei de /insumos para /servicos
    }
}