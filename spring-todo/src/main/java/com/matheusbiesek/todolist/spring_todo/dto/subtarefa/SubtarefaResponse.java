package com.matheusbiesek.todolist.spring_todo.dto.subtarefa;

import java.time.LocalDateTime;

import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response com dados da subtarefa")
public class SubtarefaResponse {

    @Schema(description = "ID da subtarefa", example = "1")
    private Long subtarefaId;

    @Schema(description = "ID da tarefa pai", example = "1")
    private Long tarefaId;

    @Schema(description = "Título da subtarefa", example = "Criar testes unitários")
    private String titulo;

    @Schema(description = "Status da subtarefa", example = "PENDENTE")
    private StatusTarefa status;

    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00")
    private LocalDateTime criadoEm;

    @Schema(description = "Data da última atualização", example = "2024-01-16T14:20:00")
    private LocalDateTime atualizadoEm;
}