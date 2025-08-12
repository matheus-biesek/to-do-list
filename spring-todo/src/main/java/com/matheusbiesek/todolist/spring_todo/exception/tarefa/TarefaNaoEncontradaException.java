package com.matheusbiesek.todolist.spring_todo.exception.tarefa;

public class TarefaNaoEncontradaException extends RuntimeException {

    public TarefaNaoEncontradaException(String message) {
        super(message);
    }

    public TarefaNaoEncontradaException(Long id) {
        super("Tarefa não encontrada com ID: " + id);
    }
}