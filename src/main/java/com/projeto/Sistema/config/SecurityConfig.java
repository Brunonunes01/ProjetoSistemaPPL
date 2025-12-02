package com.projeto.Sistema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        // 1. URLs Públicas (Login, H2, CSS/JS)
                        .requestMatchers("/login", "/h2-console/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/webjars/**").permitAll()

                        // 2. URLs de "Escrita" (só o ADMIN pode)
                        .requestMatchers("/insumos/novo", "/insumos/editar/**", "/insumos/excluir/**").hasRole("ADMIN")
                        .requestMatchers("/servicos/novo", "/servicos/editar/**", "/servicos/excluir/**").hasRole("ADMIN")
                        .requestMatchers("/insumos/salvar", "/servicos/salvar").hasRole("ADMIN")

                        // 3. URLs de "Leitura" (Qualquer um logado pode)
                        .requestMatchers("/", "/insumos", "/servicos", "/otimizador", "/otimizar", "/minimizador", "/minimizar").authenticated()

                        // 4. Trava de segurança final
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        // 💡 ALTERAÇÃO AQUI: Agora redireciona para a Dashboard (/) após logar
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }
}