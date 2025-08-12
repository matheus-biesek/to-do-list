package com.matheusbiesek.todolist.spring_todo.mapper;

import com.matheusbiesek.todolist.spring_todo.dto.subtarefa.SubtarefaCreateRequest;
import com.matheusbiesek.todolist.spring_todo.dto.subtarefa.SubtarefaResponse;
import com.matheusbiesek.todolist.spring_todo.dto.subtarefa.SubtarefaUpdateRequest;
import com.matheusbiesek.todolist.spring_todo.entity.Subtarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;
import org.springframework.stereotype.Component;

@Component
public class SubtarefaMapper {

    public Subtarefa toEntity(SubtarefaCreateRequest request, Tarefa tarefa) {
        Subtarefa subtarefa = new Subtarefa();
        subtarefa.setTitulo(request.getTitulo());
        subtarefa.setStatus(request.getStatus() != null ? request.getStatus() : StatusTarefa.PENDENTE);
        subtarefa.setTarefa(tarefa);
        return subtarefa;
    }

    public Subtarefa toEntity(SubtarefaUpdateRequest request, Subtarefa subtarefaExistente) {
        subtarefaExistente.setTitulo(request.getTitulo());
        subtarefaExistente.setStatus(request.getStatus());
        return subtarefaExistente;
    }

    public SubtarefaResponse toResponse(Subtarefa subtarefa) {
        SubtarefaResponse response = new SubtarefaResponse();
        response.setSubtarefaId(subtarefa.getSubtarefaId());
        response.setTarefaId(subtarefa.getTarefa().getTarefaId());
        response.setTitulo(subtarefa.getTitulo());
        response.setStatus(subtarefa.getStatus());
        response.setCriadoEm(subtarefa.getCriadoEm());
        response.setAtualizadoEm(subtarefa.getAtualizadoEm());
        return response;
    }
}