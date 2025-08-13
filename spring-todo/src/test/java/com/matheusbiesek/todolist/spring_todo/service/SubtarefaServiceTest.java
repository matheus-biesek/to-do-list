package com.matheusbiesek.todolist.spring_todo.service;

import com.matheusbiesek.todolist.spring_todo.entity.Subtarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import com.matheusbiesek.todolist.spring_todo.enums.Prioridade;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;
import com.matheusbiesek.todolist.spring_todo.repository.SubtarefaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubtarefaServiceTest {

    @Mock
    private SubtarefaRepository subtarefaRepository;

    @InjectMocks
    private SubtarefaService subtarefaService;

    @Test
    void deveSalvarSubtarefaComSucesso() {
        Tarefa tarefa = criarTarefa();
        Subtarefa subtarefa = criarSubtarefa(tarefa);
        
        when(subtarefaRepository.save(subtarefa)).thenReturn(subtarefa);

        Subtarefa resultado = subtarefaService.save(subtarefa);

        assertThat(resultado).isEqualTo(subtarefa);
        verify(subtarefaRepository).save(subtarefa);
    }

    @Test
    void deveBuscarSubtarefasPorTarefa() {
        Tarefa tarefa = criarTarefa();
        List<Subtarefa> subtarefas = Arrays.asList(criarSubtarefa(tarefa));
        
        when(subtarefaRepository.findByTarefa(tarefa)).thenReturn(subtarefas);

        List<Subtarefa> resultado = subtarefaService.findByTarefa(tarefa);

        assertThat(resultado).hasSize(1);
        verify(subtarefaRepository).findByTarefa(tarefa);
    }

    @Test
    void deveAtualizarStatusDaSubtarefa() {
        Subtarefa subtarefa = criarSubtarefa(criarTarefa());
        subtarefa.setSubtarefaId(1L);
        
        when(subtarefaRepository.findById(1L)).thenReturn(Optional.of(subtarefa));
        when(subtarefaRepository.save(subtarefa)).thenReturn(subtarefa);

        Subtarefa resultado = subtarefaService.updateStatus(1L, StatusTarefa.CONCLUIDA);

        assertThat(resultado.getStatus()).isEqualTo(StatusTarefa.CONCLUIDA);
        verify(subtarefaRepository).findById(1L);
        verify(subtarefaRepository).save(subtarefa);
    }

    private Tarefa criarTarefa() {
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(UUID.randomUUID());
        usuario.setNomeUsuario("testuser");
        usuario.setEmail("test@example.com");

        Tarefa tarefa = new Tarefa();
        tarefa.setTarefaId(1L);
        tarefa.setUsuario(usuario);
        tarefa.setTitulo("Tarefa Teste");
        tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa.setPrioridade(Prioridade.MEDIA);
        tarefa.setDataVencimento(LocalDate.now().plusDays(7));
        tarefa.setSubtarefas(Arrays.asList());
        return tarefa;
    }

    private Subtarefa criarSubtarefa(Tarefa tarefa) {
        Subtarefa subtarefa = new Subtarefa();
        subtarefa.setTarefa(tarefa);
        subtarefa.setTitulo("Subtarefa Teste");
        subtarefa.setStatus(StatusTarefa.PENDENTE);
        return subtarefa;
    }
}