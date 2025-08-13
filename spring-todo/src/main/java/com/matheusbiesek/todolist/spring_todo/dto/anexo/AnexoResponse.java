package com.matheusbiesek.todolist.spring_todo.dto.anexo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response com dados do anexo")
public class AnexoResponse {

    @Schema(description = "ID do anexo", example = "1")
    private Long anexoId;

    @Schema(description = "ID da tarefa", example = "1")
    private Long tarefaId;

    @Schema(description = "Nome original do arquivo", example = "documento.pdf")
    private String nomeOriginal;

    @Schema(description = "Tipo MIME do arquivo", example = "application/pdf")
    private String tipoMime;

    @Schema(description = "Tamanho do arquivo em bytes", example = "1024000")
    private Long tamanho;

    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00")
    private LocalDateTime criadoEm;
}