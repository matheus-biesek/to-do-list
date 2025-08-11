package com.matheusbiesek.todolist.spring_todo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios", schema = "app")
@Data
@EqualsAndHashCode(of = "usuarioId")
public class Usuario {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "usuario_id")
    private UUID usuarioId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "nome_usuario", unique = true, nullable = false, length = 50)
    private String nomeUsuario;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarefa> tarefas;
}