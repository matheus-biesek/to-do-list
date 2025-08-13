package com.matheusbiesek.todolist.spring_todo.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.matheusbiesek.todolist.spring_todo.dto.ErrorResponse;
import com.matheusbiesek.todolist.spring_todo.exception.anexo.AnexoNaoEncontradoException;
import com.matheusbiesek.todolist.spring_todo.exception.anexo.ArquivoVazioException;
import com.matheusbiesek.todolist.spring_todo.exception.auth.UsuarioNaoEncontradoException;
import com.matheusbiesek.todolist.spring_todo.exception.subtarefa.SubtarefaNaoEncontradaException;
import com.matheusbiesek.todolist.spring_todo.exception.tarefa.TarefaComSubtarefasPendentesException;
import com.matheusbiesek.todolist.spring_todo.exception.tarefa.TarefaNaoEncontradaException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleTarefaNaoEncontrada(TarefaNaoEncontradaException ex) {
        log.error("Tarefa não encontrada: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "TAREFA_NAO_ENCONTRADA",
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(SubtarefaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleSubtarefaNaoEncontrada(SubtarefaNaoEncontradaException ex) {
        log.error("Subtarefa não encontrada: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "SUBTAREFA_NAO_ENCONTRADA",
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(TarefaComSubtarefasPendentesException.class)
    public ResponseEntity<ErrorResponse> handleTarefaComSubtarefasPendentes(TarefaComSubtarefasPendentesException ex) {
        log.error("Tentativa de concluir tarefa com subtarefas pendentes: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "TAREFA_COM_SUBTAREFAS_PENDENTES",
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AnexoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleAnexoNaoEncontrado(AnexoNaoEncontradoException ex) {
        log.error("Anexo não encontrado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "ANEXO_NAO_ENCONTRADO",
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ArquivoVazioException.class)
    public ResponseEntity<ErrorResponse> handleArquivoVazio(ArquivoVazioException ex) {
        log.error("Arquivo vazio fornecido: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "ARQUIVO_VAZIO",
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        log.error("Usuário não encontrado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "USUARIO_NAO_ENCONTRADO",
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Erro de validação: {}", errors);

        ErrorResponse errorResponse = new ErrorResponse(
                "ERRO_VALIDACAO",
                "Dados inválidos fornecidos",
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Erro interno do servidor: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "ERRO_INTERNO",
                "Ocorreu um erro interno no servidor",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro não tratado: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "ERRO_NAO_TRATADO",
                "Ocorreu um erro inesperado",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}