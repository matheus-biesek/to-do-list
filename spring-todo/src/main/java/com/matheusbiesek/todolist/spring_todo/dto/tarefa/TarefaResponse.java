package com.matheusbiesek.todolist.spring_todo.dto.tarefa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.matheusbiesek.todolist.spring_todo.dto.subtarefa.SubtarefaResponse;
import com.matheusbiesek.todolist.spring_todo.enums.Prioridade;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response com dados da tarefa")
public class TarefaResponse {

    @Schema(description = "ID da tarefa", example = "1")
    private Long tarefaId;

    @Schema(description = "Título da tarefa", example = "Implementar nova funcionalidade")
    private String titulo;

    @Schema(description = "Descrição da tarefa", example = "Implementar CRUD de tarefas com validações")
    private String descricao;

    @Schema(description = "Data de vencimento", example = "2024-12-31")
    private LocalDate dataVencimento;

    @Schema(description = "Status da tarefa", example = "PENDENTE")
    private StatusTarefa status;

    @Schema(description = "Prioridade da tarefa", example = "ALTA")
    private Prioridade prioridade;

    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00")
    private LocalDateTime criadoEm;

    @Schema(description = "Data da última atualização", example = "2024-01-16T14:20:00")
    private LocalDateTime atualizadoEm;

    @Schema(description = "Lista de subtarefas")
    private List<SubtarefaResponse> subtarefas;

    @Schema(description = "Quantidade de subtarefas pendentes", example = "3")
    private long subtarefasPendentes;
}