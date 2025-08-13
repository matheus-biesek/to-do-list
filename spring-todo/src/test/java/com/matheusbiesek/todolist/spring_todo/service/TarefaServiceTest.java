package com.matheusbiesek.todolist.spring_todo.service;

import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import com.matheusbiesek.todolist.spring_todo.enums.Prioridade;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;
import com.matheusbiesek.todolist.spring_todo.exception.tarefa.TarefaComSubtarefasPendentesException;
import com.matheusbiesek.todolist.spring_todo.repository.TarefaRepository;
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
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaService tarefaService;

    @Test
    void deveSalvarTarefaComSucesso() {
        Usuario usuario = criarUsuario();
        Tarefa tarefa = criarTarefa(usuario);
        
        when(tarefaRepository.save(tarefa)).thenReturn(tarefa);

        Tarefa resultado = tarefaService.save(tarefa);

        assertThat(resultado).isEqualTo(tarefa);
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    void deveBuscarTarefasPorUsuario() {
        Usuario usuario = criarUsuario();
        List<Tarefa> tarefas = Arrays.asList(criarTarefa(usuario));
        
        when(tarefaRepository.findByUsuario(usuario)).thenReturn(tarefas);

        List<Tarefa> resultado = tarefaService.findByUsuario(usuario);

        assertThat(resultado).hasSize(1);
        verify(tarefaRepository).findByUsuario(usuario);
    }

    @Test
    void naoDevePermitirConcluirTarefaComSubtarefasPendentes() {
        Tarefa tarefa = criarTarefa(criarUsuario());
        tarefa.setTarefaId(1L);
        
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.hasSubtarefasPendentes(tarefa)).thenReturn(true);

        assertThatThrownBy(() -> tarefaService.updateStatus(1L, StatusTarefa.CONCLUIDA))
                .isInstanceOf(TarefaComSubtarefasPendentesException.class);

        verify(tarefaRepository, never()).save(any());
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(UUID.randomUUID());
        usuario.setNomeUsuario("testuser");
        usuario.setEmail("test@example.com");
        return usuario;
    }

    private Tarefa criarTarefa(Usuario usuario) {
        Tarefa tarefa = new Tarefa();
        tarefa.setUsuario(usuario);
        tarefa.setTitulo("Tarefa Teste");
        tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa.setPrioridade(Prioridade.MEDIA);
        tarefa.setDataVencimento(LocalDate.now().plusDays(7));
        tarefa.setSubtarefas(Arrays.asList());
        return tarefa;
    }
}