package com.matheusbiesek.todolist.spring_todo.mapper;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.matheusbiesek.todolist.spring_todo.dto.tarefa.TarefaCreateRequest;
import com.matheusbiesek.todolist.spring_todo.dto.tarefa.TarefaResponse;
import com.matheusbiesek.todolist.spring_todo.dto.tarefa.TarefaUpdateRequest;
import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;

@Component
public class TarefaMapper {

    private final SubtarefaMapper subtarefaMapper;

    public TarefaMapper(SubtarefaMapper subtarefaMapper) {
        this.subtarefaMapper = subtarefaMapper;
    }

    public Tarefa toEntity(TarefaCreateRequest request, Usuario usuario) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(request.getTitulo());
        tarefa.setDescricao(request.getDescricao());
        tarefa.setDataVencimento(request.getDataVencimento());
        tarefa.setStatus(request.getStatus() != null ? request.getStatus() : StatusTarefa.PENDENTE);
        tarefa.setPrioridade(request.getPrioridade());
        tarefa.setUsuario(usuario);
        return tarefa;
    }

    public Tarefa toEntity(TarefaUpdateRequest request, Tarefa tarefaExistente) {
        tarefaExistente.setTitulo(request.getTitulo());
        tarefaExistente.setDescricao(request.getDescricao());
        tarefaExistente.setDataVencimento(request.getDataVencimento());
        tarefaExistente.setStatus(request.getStatus());
        tarefaExistente.setPrioridade(request.getPrioridade());
        return tarefaExistente;
    }

    public TarefaResponse toResponse(Tarefa tarefa) {
        TarefaResponse response = new TarefaResponse();
        response.setTarefaId(tarefa.getTarefaId());
        response.setTitulo(tarefa.getTitulo());
        response.setDescricao(tarefa.getDescricao());
        response.setDataVencimento(tarefa.getDataVencimento());
        response.setStatus(tarefa.getStatus());
        response.setPrioridade(tarefa.getPrioridade());
        response.setCriadoEm(tarefa.getCriadoEm());
        response.setAtualizadoEm(tarefa.getAtualizadoEm());
        
        if (tarefa.getSubtarefas() != null && !tarefa.getSubtarefas().isEmpty()) {
            response.setSubtarefas(
                tarefa.getSubtarefas().stream()
                    .map(subtarefaMapper::toResponse)
                    .collect(Collectors.toList())
            );
            response.setSubtarefasPendentes(
                tarefa.getSubtarefas().stream()
                    .filter(subtarefa -> subtarefa.getStatus() != StatusTarefa.CONCLUIDA)
                    .count()
            );
        } else {
            response.setSubtarefas(Collections.emptyList());
            response.setSubtarefasPendentes(0);
        }
        
        return response;
    }
}