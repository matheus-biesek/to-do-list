package com.matheusbiesek.todolist.spring_todo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "anexos", schema = "app")
@Data
@EqualsAndHashCode(of = "anexoId")
public class Anexo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "anexo_id")
    private Long anexoId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;

    @NotBlank
    @Size(max = 255)
    @Column(name = "nome_original", nullable = false, length = 255)
    private String nomeOriginal;

    @NotBlank
    @Size(max = 255)
    @Column(name = "nome_arquivo", nullable = false, length = 255)
    private String nomeArquivo;

    @NotBlank
    @Size(max = 100)
    @Column(name = "tipo_mime", nullable = false, length = 100)
    private String tipoMime;

    @NotNull
    @Column(name = "tamanho", nullable = false)
    private Long tamanho;

    @NotBlank
    @Size(max = 500)
    @Column(name = "caminho_arquivo", nullable = false, length = 500)
    private String caminhoArquivo;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;
}