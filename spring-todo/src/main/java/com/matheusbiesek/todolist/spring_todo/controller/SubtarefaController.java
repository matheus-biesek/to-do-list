package com.matheusbiesek.todolist.spring_todo.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matheusbiesek.todolist.spring_todo.dto.subtarefa.SubtarefaCreateRequest;
import com.matheusbiesek.todolist.spring_todo.dto.subtarefa.SubtarefaResponse;
import com.matheusbiesek.todolist.spring_todo.dto.subtarefa.SubtarefaUpdateRequest;
import com.matheusbiesek.todolist.spring_todo.dto.tarefa.StatusUpdateRequest;
import com.matheusbiesek.todolist.spring_todo.entity.Subtarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;
import com.matheusbiesek.todolist.spring_todo.mapper.SubtarefaMapper;
import com.matheusbiesek.todolist.spring_todo.security.UserContext;
import com.matheusbiesek.todolist.spring_todo.service.SubtarefaService;
import com.matheusbiesek.todolist.spring_todo.service.TarefaService;
import com.matheusbiesek.todolist.spring_todo.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subtarefas")
@RequiredArgsConstructor
@Tag(name = "Subtarefas", description = "API para gerenciamento de subtarefas")
public class SubtarefaController {

    private final SubtarefaService subtarefaService;
    private final TarefaService tarefaService;
    private final UsuarioService usuarioService;
    private final SubtarefaMapper subtarefaMapper;

    @GetMapping("/tarefa/{tarefaId}")
    @Operation(summary = "Listar subtarefas de uma tarefa", 
               description = "Lista todas as subtarefas de uma tarefa específica")
    @ApiResponse(responseCode = "200", description = "Lista de subtarefas retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<List<SubtarefaResponse>> listarSubtarefasPorTarefa(
            @PathVariable Long tarefaId,
            @Parameter(description = "Filtrar por status") 
            @RequestParam(required = false) StatusTarefa status) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(tarefaId, usuario)
                .map(tarefa -> {
                    List<Subtarefa> subtarefas = status != null 
                        ? subtarefaService.findByTarefaAndStatus(tarefa, status)
                        : subtarefaService.findByTarefa(tarefa);
                    List<SubtarefaResponse> response = subtarefas.stream()
                            .map(subtarefaMapper::toResponse)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar subtarefa por ID", 
               description = "Retorna uma subtarefa específica")
    @ApiResponse(responseCode = "200", description = "Subtarefa encontrada")
    @ApiResponse(responseCode = "404", description = "Subtarefa não encontrada")
    public ResponseEntity<SubtarefaResponse> buscarSubtarefa(@PathVariable Long id) {
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return subtarefaService.findById(id)
                .filter(subtarefa -> subtarefa.getTarefa().getUsuario().getUsuarioId().equals(userId))
                .map(subtarefaMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/tarefa/{tarefaId}")
    @Operation(summary = "Criar nova subtarefa", 
               description = "Cria uma nova subtarefa para uma tarefa específica")
    @ApiResponse(responseCode = "201", description = "Subtarefa criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<SubtarefaResponse> criarSubtarefa(
            @PathVariable Long tarefaId, 
            @Valid @RequestBody SubtarefaCreateRequest request) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(tarefaId, usuario)
                .map(tarefa -> {
                    Subtarefa subtarefa = subtarefaMapper.toEntity(request, tarefa);
                    Subtarefa subtarefaSalva = subtarefaService.save(subtarefa);
                    SubtarefaResponse response = subtarefaMapper.toResponse(subtarefaSalva);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar subtarefa", 
               description = "Atualiza uma subtarefa específica")
    @ApiResponse(responseCode = "200", description = "Subtarefa atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Subtarefa não encontrada")
    public ResponseEntity<SubtarefaResponse> atualizarSubtarefa(
            @PathVariable Long id, 
            @Valid @RequestBody SubtarefaUpdateRequest request) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return subtarefaService.findById(id)
                .filter(subtarefaExistente -> subtarefaExistente.getTarefa().getUsuario().getUsuarioId().equals(userId))
                .map(subtarefaExistente -> {
                    Subtarefa subtarefaAtualizada = subtarefaMapper.toEntity(request, subtarefaExistente);
                    Subtarefa subtarefaSalva = subtarefaService.update(subtarefaAtualizada);
                    SubtarefaResponse response = subtarefaMapper.toResponse(subtarefaSalva);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da subtarefa", 
               description = "Atualiza apenas o status de uma subtarefa específica")
    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Subtarefa não encontrada")
    public ResponseEntity<SubtarefaResponse> atualizarStatusSubtarefa(
            @PathVariable Long id, 
            @Valid @RequestBody StatusUpdateRequest request) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return subtarefaService.findById(id)
                .filter(subtarefa -> subtarefa.getTarefa().getUsuario().getUsuarioId().equals(userId))
                .map(subtarefa -> {
                    Subtarefa subtarefaAtualizada = subtarefaService.updateStatus(id, request.getStatus());
                    SubtarefaResponse response = subtarefaMapper.toResponse(subtarefaAtualizada);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar subtarefa", 
               description = "Remove uma subtarefa específica")
    @ApiResponse(responseCode = "204", description = "Subtarefa deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Subtarefa não encontrada")
    public ResponseEntity<Void> deletarSubtarefa(@PathVariable Long id) {
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return subtarefaService.findById(id)
                .filter(subtarefa -> subtarefa.getTarefa().getUsuario().getUsuarioId().equals(userId))
                .map(subtarefa -> {
                    subtarefaService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tarefa/{tarefaId}/count-pendentes")
    @Operation(summary = "Contar subtarefas pendentes", 
               description = "Retorna o número de subtarefas pendentes de uma tarefa")
    @ApiResponse(responseCode = "200", description = "Contagem retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<Long> contarSubtarefasPendentes(@PathVariable Long tarefaId) {
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(tarefaId, usuario)
                .map(tarefa -> {
                    long count = subtarefaService.countSubtarefasPendentes(tarefa);
                    return ResponseEntity.ok(count);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}