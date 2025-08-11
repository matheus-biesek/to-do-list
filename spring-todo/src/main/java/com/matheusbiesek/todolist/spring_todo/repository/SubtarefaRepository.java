package com.matheusbiesek.todolist.spring_todo.repository;

import com.matheusbiesek.todolist.spring_todo.entity.Subtarefa;
import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import com.matheusbiesek.todolist.spring_todo.enums.StatusTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubtarefaRepository extends JpaRepository<Subtarefa, Long> {

    List<Subtarefa> findByTarefa(Tarefa tarefa);

    List<Subtarefa> findByTarefaAndStatus(Tarefa tarefa, StatusTarefa status);

    long countByTarefaAndStatusNot(Tarefa tarefa, StatusTarefa status);
}