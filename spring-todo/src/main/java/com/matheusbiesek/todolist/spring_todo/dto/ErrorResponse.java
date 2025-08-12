package com.matheusbiesek.todolist.spring_todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de erro")
public class ErrorResponse {
    
    @Schema(description = "Código do erro", example = "TAREFA_NAO_ENCONTRADA")
    private String code;
    
    @Schema(description = "Mensagem do erro", example = "Tarefa não encontrada")
    private String message;
    
    @Schema(description = "Timestamp do erro")
    private LocalDateTime timestamp;
    
    @Schema(description = "Detalhes do erro (usado para validações)")
    private Map<String, String> details;
    
    @Schema(description = "Lista de erros (deprecated)")
    private List<String> errors;

    public ErrorResponse(String code, String message, LocalDateTime timestamp) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ErrorResponse(String code, String message, LocalDateTime timestamp, Map<String, String> details) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
    }
}