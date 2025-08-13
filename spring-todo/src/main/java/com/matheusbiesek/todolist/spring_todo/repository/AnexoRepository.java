package com.matheusbiesek.todolist.spring_todo.repository;

import com.matheusbiesek.todolist.spring_todo.entity.Anexo;
import com.matheusbiesek.todolist.spring_todo.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnexoRepository extends JpaRepository<Anexo, Long> {

    List<Anexo> findByTarefa(Tarefa tarefa);

    Optional<Anexo> findByAnexoIdAndTarefa(Long anexoId, Tarefa tarefa);

    void deleteByAnexoIdAndTarefa(Long anexoId, Tarefa tarefa);
}