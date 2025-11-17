package com.projeto.Sistema.infrastructure.entitys.security;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario") // Nome da tabela no banco
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // O login (ex: "admin")

    @Column(nullable = false)
    private String password; // A senha HASHED (ex: $2a$10$...)

    @Column(nullable = false)
    private String role; // O nível de acesso (ex: "ROLE_ADMIN", "ROLE_USER")
}