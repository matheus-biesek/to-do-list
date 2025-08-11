package com.matheusbiesek.todolist.spring_todo.entity;

import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "subtarefas", schema = "app")
@Data
@EqualsAndHashCode(of = "subtarefaId")
public class Subtarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subtarefa_id")
    private Long subtarefaId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String titulo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTarefa status;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}