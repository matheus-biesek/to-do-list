package com.matheusbiesek.todolist.spring_todo.exception.subtarefa;

public class SubtarefaNaoEncontradaException extends RuntimeException {

    public SubtarefaNaoEncontradaException(String message) {
        super(message);
    }

    public SubtarefaNaoEncontradaException(Long id) {
        super("Subtarefa não encontrada com ID: " + id);
    }
}