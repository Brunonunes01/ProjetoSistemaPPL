package com.projeto.Sistema.controller;

import com.projeto.Sistema.business.InsumoService;
import com.projeto.Sistema.infrastructure.dto.InsumoRequest;
import com.projeto.Sistema.infrastructure.dto.InsumoResponse;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class InsumoWebController {

    private final InsumoService insumoService;

    private List<String> getUnidadesDeMedida() {
        return Arrays.asList(
                "Litro", "ml", "kg", "g", "km", "m³ (metro cúbico)",
                "Hora", "Minuto", "kWh", "Unidade(s)"
        );
    }

    @GetMapping("/insumos")
    public String listarInsumos(Model model) {
        List<InsumoResponse> insumos = insumoService.listarTodos();
        model.addAttribute("insumos", insumos);
        model.addAttribute("titulo", "Lista de Insumos (Custos Variáveis)");
        return "lista-insumos";
    }

    @GetMapping("/insumos/novo")
    public String mostrarFormularioDeCadastro(Model model) {
        model.addAttribute("insumo", new InsumoRequest());
        model.addAttribute("titulo", "Cadastrar Novo Insumo");
        model.addAttribute("unidades", getUnidadesDeMedida());
        return "form-insumo";
    }

    @GetMapping("/insumos/editar/{id}")
    public String mostrarFormularioDeEdicao(@PathVariable Long id, Model model) {
        InsumoRequest insumoDto = insumoService.buscarInsumoPorId(id);
        model.addAttribute("insumo", insumoDto);
        model.addAttribute("titulo", "Editar Insumo (ID: " + id + ")");
        model.addAttribute("unidades", getUnidadesDeMedida());
        return "form-insumo";
    }

    @GetMapping("/insumos/excluir/{id}")
    public String excluirInsumo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            insumoService.deletarInsumoPorId(id);
            redirectAttributes.addFlashAttribute("sucesso", "Insumo excluído com sucesso!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("erro", "Não é possível excluir: Este insumo faz parte de um Serviço.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir: " + e.getMessage());
        }
        return "redirect:/insumos";
    }

    @PostMapping("/insumos/salvar")
    public String salvarInsumo(@Valid @ModelAttribute("insumo") InsumoRequest request,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("titulo", request.getId() == null ? "Cadastrar Novo Insumo" : "Editar Insumo");
            model.addAttribute("unidades", getUnidadesDeMedida());
            return "form-insumo";
        }

        try {
            if (request.getId() == null) {
                insumoService.salvarInsumo(request);
                redirectAttributes.addFlashAttribute("sucesso", "Insumo cadastrado com sucesso!");
            } else {
                insumoService.atualizarInsumo(request.getId(), request);
                redirectAttributes.addFlashAttribute("sucesso", "Insumo atualizado com sucesso!");
            }
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao processar: " + e.getMessage());
            model.addAttribute("titulo", "Erro no Insumo");
            model.addAttribute("unidades", getUnidadesDeMedida());
            return "form-insumo";
        }

        return "redirect:/insumos";
    }

    // ⚠️ ATENÇÃO: O antigo método redirecionarParaServicos() (rota "/") FOI REMOVIDO DAQUI
    // para não conflitar com o HomeWebController.
}