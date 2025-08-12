package com.matheusbiesek.todolist.spring_todo.dto.tarefa;

import java.time.LocalDate;

import com.matheusbiesek.todolist.spring_todo.enums.Prioridade;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request para atualização de tarefa")
public class TarefaUpdateRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    @Schema(description = "Título da tarefa", example = "Implementar nova funcionalidade - Atualizada")
    private String titulo;

    @Schema(description = "Descrição detalhada da tarefa", example = "Implementar CRUD de tarefas com validações e testes")
    private String descricao;

    @Schema(description = "Data de vencimento da tarefa", example = "2024-12-31")
    private LocalDate dataVencimento;

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Status da tarefa", example = "EM_PROGRESSO")
    private StatusTarefa status;

    @NotNull(message = "Prioridade é obrigatória")
    @Schema(description = "Prioridade da tarefa", example = "ALTA")
    private Prioridade prioridade;
}