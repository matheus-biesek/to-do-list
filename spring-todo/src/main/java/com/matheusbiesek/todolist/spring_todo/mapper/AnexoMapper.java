package com.matheusbiesek.todolist.spring_todo.mapper;

import com.matheusbiesek.todolist.spring_todo.dto.anexo.AnexoResponse;
import com.matheusbiesek.todolist.spring_todo.entity.Anexo;
import org.springframework.stereotype.Component;

@Component
public class AnexoMapper {

    public AnexoResponse toResponse(Anexo anexo) {
        AnexoResponse response = new AnexoResponse();
        response.setAnexoId(anexo.getAnexoId());
        response.setTarefaId(anexo.getTarefa().getTarefaId());
        response.setNomeOriginal(anexo.getNomeOriginal());
        response.setTipoMime(anexo.getTipoMime());
        response.setTamanho(anexo.getTamanho());
        response.setCriadoEm(anexo.getCriadoEm());
        return response;
    }
}