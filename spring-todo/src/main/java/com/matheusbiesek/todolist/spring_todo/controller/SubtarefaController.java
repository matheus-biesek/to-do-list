package com.matheusbiesek.todolist.spring_todo.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.matheusbiesek.todolist.spring_todo.dto.common.StatusUpdateRequest;
import com.matheusbiesek.todolist.spring_todo.dto.subtarefa.SubtarefaCreateRequest;
import com.matheusbiesek.todolist.spring_todo.dto.subtarefa.SubtarefaResponse;
import com.matheusbiesek.todolist.spring_todo.dto.subtarefa.SubtarefaUpdateRequest;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subtarefas")
@RequiredArgsConstructor
@Tag(name = "Subtarefas", description = "API para gerenciamento completo de subtarefas - operações CRUD, filtros e controle de status")
public class SubtarefaController {

    private final SubtarefaService subtarefaService;
    private final TarefaService tarefaService;
    private final UsuarioService usuarioService;
    private final SubtarefaMapper subtarefaMapper;

    @GetMapping("/tarefa/{tarefaId}")
    @Operation(
        summary = "Listar subtarefas de uma tarefa com paginação", 
        description = "Recupera todas as subtarefas associadas a uma tarefa específica do usuário autenticado com suporte a paginação. " +
                     "Permite filtrar opcionalmente por status para visualizar apenas subtarefas em determinado estado."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de subtarefas recuperada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class),
                examples = @ExampleObject(
                    name = "Lista paginada de subtarefas",
                    value = "{\"content\":[{\"subtarefaId\":1,\"tarefaId\":10,\"titulo\":\"Criar testes unitários\"," +
                           "\"status\":\"PENDENTE\",\"criadoEm\":\"2024-08-13T10:30:00\"," +
                           "\"atualizadoEm\":\"2024-08-13T10:30:00\"}],\"pageable\":{\"pageNumber\":0,\"pageSize\":10}," +
                           "\"totalElements\":1,\"totalPages\":1,\"first\":true,\"last\":true}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Tarefa não encontrada ou não pertence ao usuário autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Tarefa não encontrada",
                    value = "{\"message\":\"Tarefa não encontrada\",\"status\":404}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Usuário não autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Não autenticado",
                    value = "{\"message\":\"Token de acesso inválido ou expirado\",\"status\":401}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Erro interno do servidor",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erro interno",
                    value = "{\"message\":\"Erro interno do servidor\",\"status\":500}"
                )
            )
        )
    })
    public ResponseEntity<Page<SubtarefaResponse>> listarSubtarefasPorTarefa(
            @Parameter(
                name = "tarefaId",
                description = "ID único da tarefa para listar suas subtarefas",
                required = true,
                example = "10",
                schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long tarefaId,
            
            @Parameter(
                name = "status",
                description = "Filtro opcional por status da subtarefa. Valores permitidos: PENDENTE, EM_PROGRESSO, CONCLUIDA, CANCELADA",
                required = false,
                example = "PENDENTE",
                schema = @Schema(implementation = StatusTarefa.class)
            )
            @RequestParam(required = false) StatusTarefa status,
            
            @Parameter(
                name = "page",
                description = "Número da página (inicia em 0)",
                required = false,
                example = "0",
                schema = @Schema(type = "integer", minimum = "0")
            )
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(
                name = "size",
                description = "Tamanho da página",
                required = false,
                example = "10",
                schema = @Schema(type = "integer", minimum = "1", maximum = "100")
            )
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(
                name = "sortBy",
                description = "Campo para ordenação (criadoEm, titulo, status)",
                required = false,
                example = "criadoEm",
                schema = @Schema(type = "string")
            )
            @RequestParam(defaultValue = "criadoEm") String sortBy,
            
            @Parameter(
                name = "sortDir",
                description = "Direção da ordenação (ASC ou DESC)",
                required = false,
                example = "DESC",
                schema = @Schema(type = "string", allowableValues = {"ASC", "DESC"})
            )
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Sort sort = sortDir.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);

        return tarefaService.findByIdAndUsuario(tarefaId, usuario)
                .map(tarefa -> {
                    Page<Subtarefa> subtarefas = status != null 
                        ? subtarefaService.findByTarefaAndStatus(tarefa, status, pageable)
                        : subtarefaService.findByTarefa(tarefa, pageable);
                    Page<SubtarefaResponse> response = subtarefas.map(subtarefaMapper::toResponse);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar subtarefa por ID", 
        description = "Recupera uma subtarefa específica pelo seu identificador único. " +
                     "Apenas subtarefas pertencentes ao usuário autenticado podem ser acessadas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Subtarefa encontrada e retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SubtarefaResponse.class),
                examples = @ExampleObject(
                    name = "Subtarefa encontrada",
                    value = "{\"subtarefaId\":1,\"tarefaId\":10,\"titulo\":\"Criar testes unitários\"," +
                           "\"status\":\"PENDENTE\",\"criadoEm\":\"2024-08-13T10:30:00\"," +
                           "\"atualizadoEm\":\"2024-08-13T10:30:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Subtarefa não encontrada ou não pertence ao usuário autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Subtarefa não encontrada",
                    value = "{\"message\":\"Subtarefa não encontrada\",\"status\":404}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Usuário não autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Não autenticado",
                    value = "{\"message\":\"Token de acesso inválido ou expirado\",\"status\":401}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID da subtarefa inválido",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "ID inválido",
                    value = "{\"message\":\"ID deve ser um número positivo\",\"status\":400}"
                )
            )
        )
    })
    public ResponseEntity<SubtarefaResponse> buscarSubtarefa(
            @Parameter(
                name = "id",
                description = "ID único da subtarefa a ser recuperada",
                required = true,
                example = "1",
                schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id) {
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return subtarefaService.findByIdWithTarefaAndUsuario(id)
                .filter(subtarefa -> subtarefa.getTarefa().getUsuario().getUsuarioId().equals(userId))
                .map(subtarefaMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/tarefa/{tarefaId}")
    @Operation(
        summary = "Criar nova subtarefa", 
        description = "Cria uma nova subtarefa associada a uma tarefa específica do usuário autenticado. " +
                     "A subtarefa será criada com status PENDENTE por padrão se não especificado."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Subtarefa criada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SubtarefaResponse.class),
                examples = @ExampleObject(
                    name = "Subtarefa criada",
                    value = "{\"subtarefaId\":1,\"tarefaId\":10,\"titulo\":\"Criar testes unitários\"," +
                           "\"status\":\"PENDENTE\",\"criadoEm\":\"2024-08-13T10:30:00\"," +
                           "\"atualizadoEm\":\"2024-08-13T10:30:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Dados de entrada inválidos ou violação de validação",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Dados inválidos",
                    value = "{\"message\":\"Título é obrigatório\",\"field\":\"titulo\",\"status\":400}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Tarefa não encontrada ou não pertence ao usuário autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Tarefa não encontrada",
                    value = "{\"message\":\"Tarefa não encontrada\",\"status\":404}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Usuário não autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Não autenticado",
                    value = "{\"message\":\"Token de acesso inválido ou expirado\",\"status\":401}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "Entidade não processável - dados válidos mas regra de negócio violada",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Regra de negócio violada",
                    value = "{\"message\":\"Não é possível criar subtarefa para tarefa concluída\",\"status\":422}"
                )
            )
        )
    })
    public ResponseEntity<SubtarefaResponse> criarSubtarefa(
            @Parameter(
                name = "tarefaId",
                description = "ID único da tarefa para a qual a subtarefa será criada",
                required = true,
                example = "10",
                schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long tarefaId,
            
            @Parameter(
                description = "Dados da subtarefa a ser criada",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SubtarefaCreateRequest.class),
                    examples = @ExampleObject(
                        name = "Nova subtarefa",
                        value = "{\"titulo\":\"Criar testes unitários\",\"status\":\"PENDENTE\"}"
                    )
                )
            )
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
    @Operation(
        summary = "Atualizar subtarefa completa", 
        description = "Atualiza completamente uma subtarefa existente (título e status). " +
                     "Substitui todos os dados editáveis da subtarefa pelos valores fornecidos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Subtarefa atualizada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SubtarefaResponse.class),
                examples = @ExampleObject(
                    name = "Subtarefa atualizada",
                    value = "{\"subtarefaId\":1,\"tarefaId\":10,\"titulo\":\"Criar testes unitários - Atualizada\"," +
                           "\"status\":\"EM_PROGRESSO\",\"criadoEm\":\"2024-08-13T10:30:00\"," +
                           "\"atualizadoEm\":\"2024-08-13T15:45:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Subtarefa não encontrada ou não pertence ao usuário autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Subtarefa não encontrada",
                    value = "{\"message\":\"Subtarefa não encontrada\",\"status\":404}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Dados de entrada inválidos ou violação de validação",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Dados inválidos",
                    value = "{\"message\":\"Título não pode estar vazio\",\"field\":\"titulo\",\"status\":400}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Usuário não autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Não autenticado",
                    value = "{\"message\":\"Token de acesso inválido ou expirado\",\"status\":401}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "Entidade não processável - violação de regra de negócio",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Regra de negócio violada",
                    value = "{\"message\":\"Não é possível alterar subtarefa de tarefa concluída\",\"status\":422}"
                )
            )
        )
    })
    public ResponseEntity<SubtarefaResponse> atualizarSubtarefa(
            @Parameter(
                name = "id",
                description = "ID único da subtarefa a ser atualizada",
                required = true,
                example = "1",
                schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id,
            
            @Parameter(
                description = "Dados completos da subtarefa para atualização",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SubtarefaUpdateRequest.class),
                    examples = @ExampleObject(
                        name = "Atualização de subtarefa",
                        value = "{\"titulo\":\"Criar testes unitários - Atualizada\",\"status\":\"EM_PROGRESSO\"}"
                    )
                )
            )
            @Valid @RequestBody SubtarefaUpdateRequest request) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return subtarefaService.findByIdWithTarefaAndUsuario(id)
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
    @Operation(
        summary = "Atualizar status da subtarefa", 
        description = "Atualiza apenas o status de uma subtarefa específica, mantendo os outros dados inalterados. " +
                     "Operação otimizada para mudanças rápidas de estado da subtarefa."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Status da subtarefa atualizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SubtarefaResponse.class),
                examples = @ExampleObject(
                    name = "Status atualizado",
                    value = "{\"subtarefaId\":1,\"tarefaId\":10,\"titulo\":\"Criar testes unitários\"," +
                           "\"status\":\"CONCLUIDA\",\"criadoEm\":\"2024-08-13T10:30:00\"," +
                           "\"atualizadoEm\":\"2024-08-13T16:20:00\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Subtarefa não encontrada ou não pertence ao usuário autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Subtarefa não encontrada",
                    value = "{\"message\":\"Subtarefa não encontrada\",\"status\":404}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Status inválido ou dados de entrada malformados",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Status inválido",
                    value = "{\"message\":\"Status é obrigatório\",\"field\":\"status\",\"status\":400}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Usuário não autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Não autenticado",
                    value = "{\"message\":\"Token de acesso inválido ou expirado\",\"status\":401}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "Transição de status não permitida por regras de negócio",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Transição inválida",
                    value = "{\"message\":\"Não é possível alterar status de subtarefa cancelada\",\"status\":422}"
                )
            )
        )
    })
    public ResponseEntity<SubtarefaResponse> atualizarStatusSubtarefa(
            @Parameter(
                name = "id",
                description = "ID único da subtarefa cujo status será atualizado",
                required = true,
                example = "1",
                schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id,
            
            @Parameter(
                description = "Novo status para a subtarefa",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatusUpdateRequest.class),
                    examples = @ExampleObject(
                        name = "Atualização de status",
                        value = "{\"status\":\"CONCLUIDA\"}"
                    )
                )
            )
            @Valid @RequestBody StatusUpdateRequest request) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return subtarefaService.findByIdWithTarefaAndUsuario(id)
                .filter(subtarefa -> subtarefa.getTarefa().getUsuario().getUsuarioId().equals(userId))
                .map(subtarefa -> {
                    Subtarefa subtarefaAtualizada = subtarefaService.updateStatus(id, request.getStatus());
                    SubtarefaResponse response = subtarefaMapper.toResponse(subtarefaAtualizada);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deletar subtarefa", 
        description = "Remove permanentemente uma subtarefa específica do sistema. " +
                     "Esta operação é irreversível e só pode ser executada pelo proprietário da tarefa."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Subtarefa deletada com sucesso - sem conteúdo retornado",
            content = @Content()
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Subtarefa não encontrada ou não pertence ao usuário autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Subtarefa não encontrada",
                    value = "{\"message\":\"Subtarefa não encontrada\",\"status\":404}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Usuário não autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Não autenticado",
                    value = "{\"message\":\"Token de acesso inválido ou expirado\",\"status\":401}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID da subtarefa inválido",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "ID inválido",
                    value = "{\"message\":\"ID deve ser um número positivo\",\"status\":400}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "Operação não permitida por regras de negócio",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Operação não permitida",
                    value = "{\"message\":\"Não é possível deletar subtarefa de tarefa arquivada\",\"status\":422}"
                )
            )
        )
    })
    public ResponseEntity<Void> deletarSubtarefa(
            @Parameter(
                name = "id",
                description = "ID único da subtarefa a ser removida",
                required = true,
                example = "1",
                schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id) {
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return subtarefaService.findByIdWithTarefaAndUsuario(id)
                .filter(subtarefa -> subtarefa.getTarefa().getUsuario().getUsuarioId().equals(userId))
                .map(subtarefa -> {
                    subtarefaService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tarefa/{tarefaId}/count-pendentes")
    @Operation(
        summary = "Contar subtarefas pendentes", 
        description = "Retorna o número total de subtarefas que não estão concluídas para uma tarefa específica. " +
                     "Conta subtarefas com status PENDENTE, EM_PROGRESSO e CANCELADA (tudo exceto CONCLUIDA)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Contagem de subtarefas pendentes retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "integer", format = "int64"),
                examples = @ExampleObject(
                    name = "Contagem de pendentes",
                    value = "3"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Tarefa não encontrada ou não pertence ao usuário autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Tarefa não encontrada",
                    value = "{\"message\":\"Tarefa não encontrada\",\"status\":404}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Usuário não autenticado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Não autenticado",
                    value = "{\"message\":\"Token de acesso inválido ou expirado\",\"status\":401}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID da tarefa inválido",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "ID inválido",
                    value = "{\"message\":\"ID deve ser um número positivo\",\"status\":400}"
                )
            )
        )
    })
    public ResponseEntity<Long> contarSubtarefasPendentes(
            @Parameter(
                name = "tarefaId",
                description = "ID único da tarefa para contar suas subtarefas pendentes",
                required = true,
                example = "10",
                schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long tarefaId) {
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