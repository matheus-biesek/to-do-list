package com.matheusbiesek.todolist.spring_todo.dto.subtarefa;

import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request para atualização de subtarefa")
public class SubtarefaUpdateRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    @Schema(description = "Título da subtarefa", example = "Criar testes unitários - Atualizada")
    private String titulo;

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Status da subtarefa", example = "CONCLUIDA")
    private StatusTarefa status;
}