package com.matheusbiesek.todolist.spring_todo.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.multipart.MultipartFile;

import com.matheusbiesek.todolist.spring_todo.dto.common.StatusUpdateRequest;
import com.matheusbiesek.todolist.spring_todo.dto.tarefa.TarefaCreateRequest;
import com.matheusbiesek.todolist.spring_todo.dto.tarefa.TarefaResponse;
import com.matheusbiesek.todolist.spring_todo.dto.tarefa.TarefaUpdateRequest;
import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import com.matheusbiesek.todolist.spring_todo.enums.Prioridade;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;
import com.matheusbiesek.todolist.spring_todo.mapper.TarefaMapper;
import com.matheusbiesek.todolist.spring_todo.security.UserContext;
import com.matheusbiesek.todolist.spring_todo.service.TarefaService;
import com.matheusbiesek.todolist.spring_todo.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "API para gerenciamento de tarefas")
public class TarefaController {

    private final TarefaService tarefaService;
    private final UsuarioService usuarioService;
    private final TarefaMapper tarefaMapper;

    @GetMapping
    @Operation(summary = "Listar tarefas do usuário", 
               description = "Lista todas as tarefas do usuário autenticado com opções de filtro")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso")
    public ResponseEntity<Page<TarefaResponse>> listarTarefas(
            @Parameter(description = "Filtrar por status") 
            @RequestParam(required = false) StatusTarefa status,
            @Parameter(description = "Filtrar por prioridade") 
            @RequestParam(required = false) Prioridade prioridade,
            @Parameter(description = "Filtrar por data de vencimento") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataVencimento,
            @Parameter(description = "Número da página (inicia em 0)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "criadoEm") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") 
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Sort sort = sortDir.equalsIgnoreCase("DESC") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Tarefa> tarefas = tarefaService.findByUsuarioWithFilters(
                usuario, status, prioridade, dataVencimento, pageable);
        
        Page<TarefaResponse> tarefasResponse = tarefas.map(tarefaMapper::toResponse);
        
        return ResponseEntity.ok(tarefasResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa por ID", 
               description = "Retorna uma tarefa específica do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Tarefa encontrada")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<TarefaResponse> buscarTarefa(@PathVariable Long id) {
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(id, usuario)
                .map(tarefaMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar nova tarefa", 
               description = "Cria uma nova tarefa para o usuário autenticado")
    @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<TarefaResponse> criarTarefa(@Valid @RequestBody TarefaCreateRequest request) {
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Tarefa tarefa = tarefaMapper.toEntity(request, usuario);
        Tarefa tarefaSalva = tarefaService.save(tarefa);
        TarefaResponse response = tarefaMapper.toResponse(tarefaSalva);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa", 
               description = "Atualiza uma tarefa específica do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<TarefaResponse> atualizarTarefa(
            @PathVariable Long id, 
            @Valid @RequestBody TarefaUpdateRequest request) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(id, usuario)
                .map(tarefaExistente -> {
                    Tarefa tarefaAtualizada = tarefaMapper.toEntity(request, tarefaExistente);
                    Tarefa tarefaSalva = tarefaService.update(tarefaAtualizada);
                    TarefaResponse response = tarefaMapper.toResponse(tarefaSalva);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da tarefa", 
               description = "Atualiza apenas o status de uma tarefa específica")
    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Não é possível concluir tarefa com subtarefas pendentes")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<TarefaResponse> atualizarStatusTarefa(
            @PathVariable Long id, 
            @Valid @RequestBody StatusUpdateRequest request) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(id, usuario)
                .map(tarefa -> {
                    Tarefa tarefaAtualizada = tarefaService.updateStatus(id, request.getStatus());
                    TarefaResponse response = tarefaMapper.toResponse(tarefaAtualizada);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar tarefa", 
               description = "Remove uma tarefa específica do usuário autenticado")
    @ApiResponse(responseCode = "204", description = "Tarefa deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long id) {
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(id, usuario)
                .map(tarefa -> {
                    tarefaService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/vencidas")
    @Operation(summary = "Listar tarefas vencidas", 
               description = "Lista todas as tarefas vencidas do usuário autenticado com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas vencidas com paginação")
    public ResponseEntity<Page<TarefaResponse>> listarTarefasVencidas(
            @Parameter(description = "Número da página (inicia em 0)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") 
            @RequestParam(defaultValue = "criadoEm") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") 
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Sort sort = sortDir.equalsIgnoreCase("DESC") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Tarefa> tarefasVencidas = tarefaService.findTarefasVencidas(usuario, pageable);
        Page<TarefaResponse> response = tarefasVencidas.map(tarefaMapper::toResponse);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/anexo")
    @Operation(summary = "Upload de anexo", 
               description = "Faz upload de um arquivo anexo para uma tarefa")
    @ApiResponse(responseCode = "200", description = "Anexo enviado com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<String> uploadAnexo(
            @PathVariable Long id, 
            @RequestParam("arquivo") MultipartFile arquivo) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(id, usuario)
                .map(tarefa -> {
                    // TODO: Implementar lógica de upload de arquivo
                    String nomeArquivo = arquivo.getOriginalFilename();
                    return ResponseEntity.ok("Arquivo " + nomeArquivo + " enviado com sucesso para a tarefa " + id);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}