package com.matheusbiesek.todolist.spring_todo.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.matheusbiesek.todolist.spring_todo.dto.anexo.AnexoResponse;
import com.matheusbiesek.todolist.spring_todo.entity.Anexo;
import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import com.matheusbiesek.todolist.spring_todo.mapper.AnexoMapper;
import com.matheusbiesek.todolist.spring_todo.security.UserContext;
import com.matheusbiesek.todolist.spring_todo.service.AnexoService;
import com.matheusbiesek.todolist.spring_todo.service.TarefaService;
import com.matheusbiesek.todolist.spring_todo.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tarefas/{tarefaId}/anexos")
@RequiredArgsConstructor
@Tag(name = "Anexos", description = "API para gerenciamento de anexos de tarefas")
public class AnexoController {

    private final AnexoService anexoService;
    private final TarefaService tarefaService;
    private final UsuarioService usuarioService;
    private final AnexoMapper anexoMapper;

    @GetMapping
    @Operation(summary = "Listar anexos da tarefa", 
               description = "Lista todos os anexos de uma tarefa específica")
    @ApiResponse(responseCode = "200", description = "Lista de anexos retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<List<AnexoResponse>> listarAnexos(@PathVariable Long tarefaId) {
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(tarefaId, usuario)
                .map(tarefa -> {
                    List<Anexo> anexos = anexoService.findByTarefa(tarefa);
                    List<AnexoResponse> anexosResponse = anexos.stream()
                            .map(anexoMapper::toResponse)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(anexosResponse);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Upload de anexo", 
               description = "Faz upload de um arquivo anexo para uma tarefa")
    @ApiResponse(responseCode = "201", description = "Anexo enviado com sucesso")
    @ApiResponse(responseCode = "400", description = "Arquivo inválido")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    public ResponseEntity<AnexoResponse> uploadAnexo(
            @PathVariable Long tarefaId,
            @Parameter(description = "Arquivo a ser anexado") 
            @RequestParam("arquivo") MultipartFile arquivo) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(tarefaId, usuario)
                .map(tarefa -> {
                    Anexo anexoSalvo = anexoService.salvarAnexo(arquivo, tarefa);
                    AnexoResponse response = anexoMapper.toResponse(anexoSalvo);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{anexoId}/download")
    @Operation(summary = "Download de anexo", 
               description = "Faz download de um anexo específico da tarefa")
    @ApiResponse(responseCode = "200", description = "Arquivo baixado com sucesso")
    @ApiResponse(responseCode = "404", description = "Anexo ou tarefa não encontrada")
    public ResponseEntity<Resource> downloadAnexo(
            @PathVariable Long tarefaId,
            @PathVariable Long anexoId) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(tarefaId, usuario)
                .map(tarefa -> {
                    return anexoService.findByIdAndTarefa(anexoId, tarefa)
                            .map(anexo -> {
                                Resource resource = anexoService.carregarAnexo(anexoId, tarefa);
                                
                                return ResponseEntity.ok()
                                        .contentType(MediaType.parseMediaType(anexo.getTipoMime()))
                                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                               "attachment; filename=\"" + anexo.getNomeOriginal() + "\"")
                                        .body(resource);
                            })
                            .orElse(ResponseEntity.notFound().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{anexoId}")
    @Operation(summary = "Deletar anexo", 
               description = "Remove um anexo específico da tarefa")
    @ApiResponse(responseCode = "204", description = "Anexo deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Anexo ou tarefa não encontrada")
    public ResponseEntity<Void> deletarAnexo(
            @PathVariable Long tarefaId,
            @PathVariable Long anexoId) {
        
        UUID userId = UserContext.getUserId();
        Usuario usuario = usuarioService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return tarefaService.findByIdAndUsuario(tarefaId, usuario)
                .map(tarefa -> {
                    return anexoService.findByIdAndTarefa(anexoId, tarefa)
                            .map(anexo -> {
                                anexoService.deletarAnexo(anexoId, tarefa);
                                return ResponseEntity.noContent().<Void>build();
                            })
                            .orElse(ResponseEntity.notFound().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }
}