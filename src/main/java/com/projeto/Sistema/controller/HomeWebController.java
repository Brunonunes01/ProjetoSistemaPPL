package com.projeto.Sistema.controller;

import com.projeto.Sistema.infrastructure.repository.InsumoRepository;
import com.projeto.Sistema.infrastructure.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeWebController {

    private final InsumoRepository insumoRepository;
    private final ServicoRepository servicoRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Usa count() para evitar buscar listas inteiras desnecessariamente
        long qtdInsumos = insumoRepository.count();
        long qtdServicos = servicoRepository.count();

        model.addAttribute("qtdInsumos", qtdInsumos);
        model.addAttribute("qtdServicos", qtdServicos);

        return "home"; // Vai procurar o home.html
    }
}