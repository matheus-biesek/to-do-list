package com.matheusbiesek.todolist.spring_todo.service;

import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import com.matheusbiesek.todolist.spring_todo.enums.Prioridade;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;
import com.matheusbiesek.todolist.spring_todo.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    @Transactional(readOnly = true)
    public List<Tarefa> findAll() {
        return tarefaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Tarefa> findById(Long id) {
        try {
            return tarefaRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar tarefa por ID: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Tarefa> findByUsuario(Usuario usuario) {
        try {
            return tarefaRepository.findByUsuario(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar tarefas do usuário: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Tarefa> findByUsuarioWithFilters(Usuario usuario, StatusTarefa status, 
                                                 Prioridade prioridade, LocalDate dataVencimento) {
        try {
            return tarefaRepository.findByUsuarioWithFilters(usuario, status, prioridade, dataVencimento);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar tarefas com filtros: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Tarefa> findByUsuarioWithFilters(Usuario usuario, StatusTarefa status, 
                                                 Prioridade prioridade, LocalDate dataVencimento, Pageable pageable) {
        try {
            Page<Tarefa> tarefas = tarefaRepository.findByUsuarioWithFilters(usuario, status, prioridade, dataVencimento, pageable);
            tarefas.getContent().forEach(tarefa -> tarefa.getSubtarefas().size());
            return tarefas;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar tarefas com filtros paginadas: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Tarefa> findByIdAndUsuario(Long id, Usuario usuario) {
        try {
            Optional<Tarefa> tarefaOpt = tarefaRepository.findByTarefaIdAndUsuario(id, usuario);
            if (tarefaOpt.isPresent()) {
                Tarefa tarefa = tarefaOpt.get();
                tarefa.getSubtarefas().size();
            }
            return tarefaOpt;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar tarefa por ID e usuário: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Tarefa> findByUsuarioAndStatus(Usuario usuario, StatusTarefa status) {
        try {
            return tarefaRepository.findByUsuarioAndStatus(usuario, status);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar tarefas por status: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Tarefa> findByUsuarioAndPrioridade(Usuario usuario, Prioridade prioridade) {
        try {
            return tarefaRepository.findByUsuarioAndPrioridade(usuario, prioridade);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar tarefas por prioridade: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Tarefa> findTarefasVencidas(Usuario usuario) {
        try {
            List<Tarefa> tarefas = tarefaRepository.findByUsuarioAndDataVencimentoBefore(usuario, LocalDate.now());
            tarefas.forEach(tarefa -> tarefa.getSubtarefas().size());
            return tarefas;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar tarefas vencidas: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Tarefa> findTarefasVencidas(Usuario usuario, Pageable pageable) {
        try {
            Page<Tarefa> tarefas = tarefaRepository.findByUsuarioAndDataVencimentoBefore(usuario, LocalDate.now(), pageable);
            tarefas.getContent().forEach(tarefa -> tarefa.getSubtarefas().size());
            return tarefas;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar tarefas vencidas paginadas: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Tarefa save(Tarefa tarefa) {
        try {
            return tarefaRepository.save(tarefa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar tarefa: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Tarefa update(Tarefa tarefa) {
        try {
            if (!tarefaRepository.existsById(tarefa.getTarefaId())) {
                throw new RuntimeException("Tarefa não encontrada para atualização");
            }
            return tarefaRepository.save(tarefa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar tarefa: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Tarefa updateStatus(Long tarefaId, StatusTarefa novoStatus) {
        try {
            Optional<Tarefa> tarefaOpt = tarefaRepository.findById(tarefaId);
            if (tarefaOpt.isEmpty()) {
                throw new RuntimeException("Tarefa não encontrada");
            }

            Tarefa tarefa = tarefaOpt.get();
            tarefa.getSubtarefas().size();

            if (novoStatus == StatusTarefa.CONCLUIDA && hasSubtarefasPendentes(tarefa)) {
                throw new RuntimeException("Não é possível concluir tarefa com subtarefas pendentes");
            }

            tarefa.setStatus(novoStatus);
            return tarefaRepository.save(tarefa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar status da tarefa: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            if (!tarefaRepository.existsById(id)) {
                throw new RuntimeException("Tarefa não encontrada para exclusão");
            }
            tarefaRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar tarefa: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public boolean hasSubtarefasPendentes(Tarefa tarefa) {
        try {
            return tarefaRepository.hasSubtarefasPendentes(tarefa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar subtarefas pendentes: " + e.getMessage(), e);
        }
    }
}