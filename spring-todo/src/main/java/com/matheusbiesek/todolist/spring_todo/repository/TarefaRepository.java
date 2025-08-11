package com.matheusbiesek.todolist.spring_todo.repository;

import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import com.matheusbiesek.todolist.spring_todo.enums.Prioridade;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByUsuario(Usuario usuario);

    List<Tarefa> findByUsuarioAndStatus(Usuario usuario, StatusTarefa status);

    List<Tarefa> findByUsuarioAndPrioridade(Usuario usuario, Prioridade prioridade);

    List<Tarefa> findByUsuarioAndDataVencimento(Usuario usuario, LocalDate dataVencimento);

    List<Tarefa> findByUsuarioAndDataVencimentoBefore(Usuario usuario, LocalDate data);

    @Query("SELECT t FROM Tarefa t WHERE t.usuario = :usuario " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:prioridade IS NULL OR t.prioridade = :prioridade) " +
           "AND (:dataVencimento IS NULL OR t.dataVencimento = :dataVencimento)")
    List<Tarefa> findByUsuarioWithFilters(@Param("usuario") Usuario usuario,
                                         @Param("status") StatusTarefa status,
                                         @Param("prioridade") Prioridade prioridade,
                                         @Param("dataVencimento") LocalDate dataVencimento);

    @Query("SELECT COUNT(s) > 0 FROM Subtarefa s WHERE s.tarefa = :tarefa AND s.status != 'CONCLUIDA'")
    boolean hasSubtarefasPendentes(@Param("tarefa") Tarefa tarefa);
}