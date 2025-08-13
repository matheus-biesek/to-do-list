package com.matheusbiesek.todolist.spring_todo.repository;

import com.matheusbiesek.todolist.spring_todo.entity.Subtarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubtarefaRepository extends JpaRepository<Subtarefa, Long> {

    List<Subtarefa> findByTarefa(Tarefa tarefa);

    Page<Subtarefa> findByTarefa(Tarefa tarefa, Pageable pageable);

    List<Subtarefa> findByTarefaAndStatus(Tarefa tarefa, StatusTarefa status);

    Page<Subtarefa> findByTarefaAndStatus(Tarefa tarefa, StatusTarefa status, Pageable pageable);

    long countByTarefaAndStatusNot(Tarefa tarefa, StatusTarefa status);

    @Query("SELECT s FROM Subtarefa s JOIN FETCH s.tarefa t JOIN FETCH t.usuario WHERE s.subtarefaId = :id")
    Optional<Subtarefa> findByIdWithTarefaAndUsuario(@Param("id") Long id);
}