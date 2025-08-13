package com.matheusbiesek.todolist.spring_todo.dto.common;

import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request para atualização de status")
public class StatusUpdateRequest {

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Novo status", example = "CONCLUIDA")
    private StatusTarefa status;
}