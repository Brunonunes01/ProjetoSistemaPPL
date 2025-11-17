package com.projeto.Sistema.business.security;

import com.projeto.Sistema.infrastructure.entitys.security.Usuario;
import com.projeto.Sistema.infrastructure.repository.security.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Este é o método que o Spring Security chama quando alguém tenta logar.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Busca o usuário no banco pelo username
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // 2. Converte nosso 'Usuario' (Entidade) para o 'User' (do Spring Security)
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                // Converte a 'role' (ex: "ROLE_ADMIN") em uma "Autoridade"
                Collections.singletonList(new SimpleGrantedAuthority(usuario.getRole()))
        );
    }
}