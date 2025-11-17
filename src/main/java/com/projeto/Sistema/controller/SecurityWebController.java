package com.projeto.Sistema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityWebController {

    /**
     * Este é o método que estava faltando.
     * Quando o Spring Security redireciona para "/login", este método
     * atende a requisição e retorna o nome do arquivo HTML.
     */
    @GetMapping("/login")
    public String login() {
        return "login"; // -> /resources/templates/login.html
    }
}