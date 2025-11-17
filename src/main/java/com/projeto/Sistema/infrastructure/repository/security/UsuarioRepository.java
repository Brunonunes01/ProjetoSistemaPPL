package com.projeto.Sistema.infrastructure.repository.security;

import com.projeto.Sistema.infrastructure.entitys.security.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método que o Spring Security vai usar para buscar o usuário pelo login
    Optional<Usuario> findByUsername(String username);
}