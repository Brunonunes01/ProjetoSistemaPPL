package com.projeto.Sistema;

import com.projeto.Sistema.infrastructure.entitys.security.Usuario;
import com.projeto.Sistema.infrastructure.repository.security.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class SistemaApplication {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SistemaApplication.class, args);
    }

    @Bean
    public CommandLineRunner createDefaultUser() {
        return args -> {
            // 1. Cria o usuário ADMIN (Dono)
            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                String senhaCriptografada = passwordEncoder.encode("admin");

                Usuario admin = Usuario.builder()
                        .username("admin")
                        .password(senhaCriptografada)
                        .role("ROLE_ADMIN")
                        .build();

                usuarioRepository.save(admin);
                System.out.println(">>> USUÁRIO 'admin' CRIADO COM SENHA 'admin' <<<");
            }

            // 💡 --- ADIÇÃO --- 💡
            // 2. Cria o usuário USER (Funcionário)
            if (usuarioRepository.findByUsername("user").isEmpty()) {
                String senhaCriptografada = passwordEncoder.encode("user");

                Usuario user = Usuario.builder()
                        .username("user")
                        .password(senhaCriptografada)
                        .role("ROLE_USER") // A permissão é diferente
                        .build();

                usuarioRepository.save(user);
                System.out.println(">>> USUÁRIO 'user' CRIADO COM SENHA 'user' <<<");
            }
            // 💡 --- FIM DA ADIÇÃO --- 💡
        };
    }
}