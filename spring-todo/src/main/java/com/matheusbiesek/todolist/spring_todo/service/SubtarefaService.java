package com.matheusbiesek.todolist.spring_todo.service;

import com.matheusbiesek.todolist.spring_todo.entity.Subtarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;
import com.matheusbiesek.todolist.spring_todo.repository.SubtarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubtarefaService {

    private final SubtarefaRepository subtarefaRepository;

    @Transactional(readOnly = true)
    public List<Subtarefa> findAll() {
        return subtarefaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Subtarefa> findById(Long id) {
        try {
            return subtarefaRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar subtarefa por ID: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Subtarefa> findByIdWithTarefaAndUsuario(Long id) {
        try {
            return subtarefaRepository.findByIdWithTarefaAndUsuario(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar subtarefa com tarefa e usuário: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Subtarefa> findByTarefa(Tarefa tarefa) {
        try {
            return subtarefaRepository.findByTarefa(tarefa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar subtarefas da tarefa: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Subtarefa> findByTarefaAndStatus(Tarefa tarefa, StatusTarefa status) {
        try {
            return subtarefaRepository.findByTarefaAndStatus(tarefa, status);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar subtarefas por status: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Subtarefa> findByTarefa(Tarefa tarefa, Pageable pageable) {
        try {
            return subtarefaRepository.findByTarefa(tarefa, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar subtarefas paginadas da tarefa: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Subtarefa> findByTarefaAndStatus(Tarefa tarefa, StatusTarefa status, Pageable pageable) {
        try {
            return subtarefaRepository.findByTarefaAndStatus(tarefa, status, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar subtarefas paginadas por status: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public long countSubtarefasPendentes(Tarefa tarefa) {
        try {
            return subtarefaRepository.countByTarefaAndStatusNot(tarefa, StatusTarefa.CONCLUIDA);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao contar subtarefas pendentes: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Subtarefa save(Subtarefa subtarefa) {
        try {
            return subtarefaRepository.save(subtarefa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar subtarefa: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Subtarefa update(Subtarefa subtarefa) {
        try {
            if (!subtarefaRepository.existsById(subtarefa.getSubtarefaId())) {
                throw new RuntimeException("Subtarefa não encontrada para atualização");
            }
            return subtarefaRepository.save(subtarefa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar subtarefa: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Subtarefa updateStatus(Long subtarefaId, StatusTarefa novoStatus) {
        try {
            Optional<Subtarefa> subtarefaOpt = subtarefaRepository.findById(subtarefaId);
            if (subtarefaOpt.isEmpty()) {
                throw new RuntimeException("Subtarefa não encontrada");
            }

            Subtarefa subtarefa = subtarefaOpt.get();
            subtarefa.setStatus(novoStatus);
            return subtarefaRepository.save(subtarefa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar status da subtarefa: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            if (!subtarefaRepository.existsById(id)) {
                throw new RuntimeException("Subtarefa não encontrada para exclusão");
            }
            subtarefaRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar subtarefa: " + e.getMessage(), e);
        }
    }
}