package com.matheusbiesek.todolist.spring_todo.dto.subtarefa;

import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request para criação de subtarefa")
public class SubtarefaCreateRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    @Schema(description = "Título da subtarefa", example = "Criar testes unitários")
    private String titulo;

    @Schema(description = "Status inicial da subtarefa (opcional, padrão: PENDENTE)", example = "PENDENTE")
    private StatusTarefa status;
}